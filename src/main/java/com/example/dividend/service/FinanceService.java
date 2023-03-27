package com.example.dividend.service;

import com.example.dividend.exception.impl.NoCompanException;
import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividenRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividenRepository dividenRepository;


    @Cacheable(key="#companyName",value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividenByCompanyName(String companyName) {

        //1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanException());

        //2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntityList = this.dividenRepository.findAllBycompanyId(company.getId());


        List<Dividend> dividends = dividendEntityList.stream()
                .map(e-> new Dividend( e.getDate(), e.getDividend()))
                .collect(Collectors.toList());
//        List<Dividend> dividends = new ArrayList<>();
//        for (var entity : dividendEntityList) {
//            dividends.add(Dividend.builder()
//                    .date(entity.getDate())
//                    .dividend(entity.getDividend())
//                    .build());
//        }

        // 3. 결과 조합 후 반환
        return new ScrapedResult( new Company(company.getTicker(),company.getName()),
                 dividends);
    }
}
