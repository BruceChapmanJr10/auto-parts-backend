package com.example.demo.service.affiliate;


import com.example.demo.model.dto.ListingResponse;
import org.springframework.stereotype.Service;

@Service
public class AffiliateLinkService {

    //  affiliate IDs (replace later)
    private static final String EBAY_CAMPAIGN_ID = "1234567890";
    private static final String AMAZON_TAG = "yourtag-20";

    public ListingResponse inject(ListingResponse listing) {

        String url = listing.getProductUrl();

        if (url.contains("ebay.com")) {
            listing.setProductUrl(
                    url + "?campid=" + EBAY_CAMPAIGN_ID
            );
        }

        if (url.contains("amazon.com")) {
            listing.setProductUrl(
                    url + "?tag=" + AMAZON_TAG
            );
        }

        return listing;
    }
}
