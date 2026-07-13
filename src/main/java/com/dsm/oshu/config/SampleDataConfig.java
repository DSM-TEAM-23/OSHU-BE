package com.dsm.oshu.config;

import com.dsm.oshu.domain.store.Promotion;
import com.dsm.oshu.domain.store.PromotionRepository;
import com.dsm.oshu.domain.store.Store;
import com.dsm.oshu.domain.store.StoreRepository;
import com.dsm.oshu.domain.store.TimeSale;
import com.dsm.oshu.domain.store.TimeSaleRepository;
import java.time.LocalDateTime;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDataConfig {
    @Bean
    ApplicationRunner sampleData(StoreRepository stores, PromotionRepository promotions, TimeSaleRepository timeSales) {
        return args -> {
            if (stores.count() > 0) return;
            Store bakery = stores.save(new Store("오슈 베이커리", "카페", "갓 구운 빵과 커피를 판매합니다.",
                    "대전광역시 유성구 궁동 123", 36.3622, 127.3449, "042-000-0001", "09:00 - 21:00", "owner"));
            Store kitchen = stores.save(new Store("유성 키친", "음식점", "점심 타임에 빠르게 즐기는 한식 식당입니다.",
                    "대전광역시 유성구 대학로 99", 36.3613, 127.3459, "042-000-0002", "11:00 - 22:00", "owner"));
            LocalDateTime now = LocalDateTime.now();
            promotions.save(new Promotion(bakery, "EVENT", "오늘의 빵 50% 할인", "당일 생산 빵을 마감 전 할인합니다.", null,
                    now.minusHours(1), now.plusHours(3)));
            timeSales.save(new TimeSale(bakery, "클래식 아몬드 크루아상", 4500, 2800,
                    now.minusMinutes(30), now.plusHours(2), "매장 방문 고객 대상"));
            promotions.save(new Promotion(kitchen, "COUPON", "점심 메뉴 무료 음료", "점심 식사 주문 시 음료를 제공합니다.", null,
                    now.minusHours(1), now.plusDays(7)));
        };
    }
}
