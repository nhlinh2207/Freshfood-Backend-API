package com.linh.freshfoodbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linh.freshfoodbackend.annotation.Backup;
import com.linh.freshfoodbackend.entity.ServerLog;
import com.linh.freshfoodbackend.repository.IServerLogRepo;
import com.linh.freshfoodbackend.service.IServerLogService;
import com.linh.freshfoodbackend.utils.ContextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@Service
@Slf4j
public class ServerLogService implements IServerLogService {

    private final IServerLogRepo serverLogRepo;
    private final ContextUtil contextUtil;

    @Override
    public <E> void createLog(E backupObject, String code, String type, String content, String reason) throws IllegalAccessException, JsonProcessingException {
        // Duyệt từng field của backupObject, tìm những field có đánh dấu @Backup để lưu vào trường oldValue của ServerLog
        Class<?> backupObjectClass = backupObject.getClass();
        Field[] fields = backupObjectClass.getFields();
        Map<String, Object> oldValueFields = new LinkedHashMap<>();
        for(Field field : fields){
            field.setAccessible(true);
            if (field.isAnnotationPresent(Backup.class))
                oldValueFields.put(field.getName(), field.get(backupObject));
        }

        serverLogRepo.save(
                ServerLog.builder()
                .code(code)
                .type(type)
                .oldValue(new ObjectMapper().writeValueAsString(backupObject))
                .ip(contextUtil.getIpRequest())
                .createTime(new Date())
                .content(content)
                .reason(reason)
                .build()
        );
    }
}
