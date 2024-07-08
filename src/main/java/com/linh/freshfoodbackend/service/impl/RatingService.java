package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.RankDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.Product;
import com.linh.freshfoodbackend.entity.Rank;
import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.IProductRepo;
import com.linh.freshfoodbackend.repository.IRatingRepo;
import com.linh.freshfoodbackend.service.IRatingService;
import com.linh.freshfoodbackend.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class RatingService implements IRatingService {

    private final IRatingRepo ratingRepo;
    private final IUserService userService;
    private final IProductRepo productRepo;

    @Override
    public ResponseObject<String> create(RankDto req) {
        try {
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
//            User currentUser = userService.getCurrentLoginUser();
            User currentUser = userService.findByEmail(req.getSenderEmail());
            Product product = productRepo.findById(req.getProductId()).get();
            Rank rank = Rank.builder()
                    .rankContent(req.getRankContent())
                    .rankNumber(req.getRankNumber())
                    .rankCustomerName(req.getRankCustomerName())
                    .product(product)
                    .user(currentUser)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            ratingRepo.saveAndFlush(rank);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<Integer> getAverageValueOfProduct(Integer productId) {
        try{
            ResponseObject<Integer> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Float averageValue = ratingRepo.getAverageValueOfProduct(productId);
            response.setData(averageValue != null ? Math.round(averageValue) : 0);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public Integer getTotalRanks() {
        return ratingRepo.geTotalRank();
    }
}
