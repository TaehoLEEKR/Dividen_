package com.example.dividend.service;


import com.example.dividend.exception.impl.NoCompanException;
import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividenRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividenRepository dividenRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllcompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }
        //해당 회사가 존재할경우 회사 배당금 정보 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
        this.dividenRepository.saveAll(dividendEntities);

        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword){

        Pageable limit = PageRequest.of(0,10);

        Page<CompanyEntity> companyEntities =
        this.companyRepository.findByNameStartingWithIgnoreCase(keyword,limit);

        return companyEntities.stream().map(e->e.getName())
                .collect(Collectors.toList());
    }

    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword,null);
    }
    public List<String> autocomplete(String keyword){
       return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }
    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        var company = this.companyRepository.findByTicker(ticker).orElseThrow( () -> new NoCompanException());

        this.dividenRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);
        this.deleteAutocompleteKeyword(company.getName());
        return company.getName();
    }
}
