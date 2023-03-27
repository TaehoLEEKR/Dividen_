package com.example.dividend.scheduler;

import arrow.typeclasses.Divide;
import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividenRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@EnableCaching
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScrapper;
    private  final DividenRepository dividenRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
//    @Scheduled(cron = "${scheduler.scrap.yahoo} *")
    public void yahooFinanceScheduling(){

        List<CompanyEntity> companies =  this.companyRepository.findAll();

        for(var company : companies){
           log.info("scrapping scheduler is started -> "+company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScrapper.scrap(new Company(company.getTicker(), company.getName()));
            scrapedResult.getDividends().stream()
                    .map(e -> new DividendEntity(company.getId(),e))
                    .forEach(e->{
                        boolean exists = this.dividenRepository.existsByCompanyIdAndDate(e.getCompanyId(),e.getDate());
                        if(!exists){
                            this.dividenRepository.save(e);
                            log.info("insert new dividened -> " + e.toString());
                        }
                    });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }


}
