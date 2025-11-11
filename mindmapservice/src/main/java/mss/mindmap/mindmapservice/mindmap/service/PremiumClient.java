package mss.mindmap.mindmapservice.mindmap.service;

import mss.mindmap.mindmapservice.mindmap.dto.response.PremiumResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "premium-service", url = "${gateway.url.premium}")
@Service
public interface PremiumClient {

    @GetMapping("/premium/premiums/{userId}")
    public ResponseEntity<PremiumResponse> getPremiumByUserId(@PathVariable String userId) ;

}
