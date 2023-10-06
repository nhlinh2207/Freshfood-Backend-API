package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.Category;
import com.linh.freshfoodbackend.entity.Product;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.ICartRepo;
import com.linh.freshfoodbackend.repository.ICategoryRepo;
import com.linh.freshfoodbackend.repository.IRatingRepo;
import com.linh.freshfoodbackend.repository.IUserRepo;
import com.linh.freshfoodbackend.service.IChartService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ChartService implements IChartService {

    private final IUserRepo userRepo;
    private final ICartRepo cartRepo;
    private final IRatingRepo rankRepo;
    private final ICategoryRepo categoryRepo;

    @Override
    public ResponseObject<Map<String, Integer>> cardChart() {
        try{
            ResponseObject<Map<String, Integer>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Map<String, Integer> data = new LinkedHashMap<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int month = cal.get(Calendar.MONTH)+1;
            int year = cal.get(Calendar.YEAR);
            // Get annual income
            data.put("annualIncome", cartRepo.getAnnualIncome(year));
            // Get monthly income
            data.put("monthlyIncome", cartRepo.getMonthIncome(year, month));
            // Get total user
            data.put("totalUser", userRepo.getTotalUser());
            // Get total Rank
            data.put("totalRank", rankRepo.geTotalRank());

            response.setData(data);
            return  response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<Collection<Object>> areaChart() {
        try{
            ResponseObject<Collection<Object>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int year = cal.get(Calendar.YEAR);
            List<Map<String, Object>> queryResult = cartRepo.listMonthlyIncomeByYear(year);
            Map<Integer, Object> data = new TreeMap<>();
            int[] months = new int[12];
            Arrays.fill(months, 0);
            for (Map<String, Object> item : queryResult){
                int itemMonth = (Integer) item.get("month");
                data.put(itemMonth, item.get("total"));
                months[itemMonth - 1] = 1;
            }
            for (int i = 0; i< months.length; i++){
                if (months[i] == 0)
                    data.put(i+1, 0);
            }
            response.setData(data.values());
            return  response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<Map<String, Integer>> pieChart() {
        try{
            ResponseObject<Map<String, Integer>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Map<String, Integer> data = new LinkedHashMap<>();
            List<Category> categories = categoryRepo.findAll();
            categories.forEach(c -> {
                data.put(c.getName(), c.getProducts().stream().mapToInt(Product::getBuyingCount).sum());
            });
            response.setData(data);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }
}
