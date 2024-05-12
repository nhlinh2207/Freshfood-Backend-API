package com.linh.freshfoodbackend.dto.sqlSearchDto;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class GenericRsqlSpecification<T> implements Specification<T> {

    private String property;
    private ComparisonOperator operator;
    private List<String> arguments;

    @SneakyThrows
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
                                 CriteriaBuilder builder) {
        Path<String> propertyExpression = parseProperty(root);
        String javaType = String.valueOf(propertyExpression.getJavaType());
        List<Object> args = castArguments(propertyExpression);
        Object argument = args.get(0);

        DateCheckInfo dateCheck = DateUtil.checkAndGetDate(argument);
        if (dateCheck.getIsDateFormat()) argument = dateCheck.getDate();

        switch (RsqlSearchOperation.getSimpleOperator(operator)) {
            case EQUAL:
                if (String.valueOf(argument).equals(SearchConstant.NUMBER_NULL))
                    return builder.isNull(propertyExpression);
                if (String.valueOf(argument).equals(SearchConstant.OBJECT_NULL))
                    return builder.isNull(builder.lower(propertyExpression));
                if (String.valueOf(argument).equals(SearchConstant.OBJECT_NOT_NULL))
                    return builder.isNotNull(builder.lower(propertyExpression));
                if (isDouble(javaType))
                    return builder.equal(propertyExpression, argument);
                if (argument instanceof String)
                    return builder.like(builder.lower(propertyExpression),
                            formatArgument(argument), '\\');
                else if (argument == null)
                    return builder.isNull(propertyExpression);
                else return builder.equal(propertyExpression, argument);


            case NOT_EQUAL:
                if (argument instanceof String)
                    return builder.notLike(propertyExpression,
                            argument.toString().replace('*', '%'));
                else if (argument == null)
                    return builder.isNotNull(propertyExpression);
                else return builder.notEqual(propertyExpression, argument);

            case GREATER_THAN:
                if (argument instanceof ZonedDateTime)
                    return builder.greaterThan(propertyExpression.as(ZonedDateTime.class),
                            (ZonedDateTime) argument);
                return builder.greaterThan(propertyExpression,
                        argument.toString());

            case GREATER_THAN_OR_EQUAL:
                if (argument instanceof ZonedDateTime)
                    return builder.greaterThanOrEqualTo(propertyExpression.as(ZonedDateTime.class),
                            (ZonedDateTime) argument);
                return builder.greaterThanOrEqualTo(propertyExpression,
                        argument.toString());

            case LESS_THAN:
                if (argument instanceof ZonedDateTime)
                    return builder.lessThan(propertyExpression.as(ZonedDateTime.class),
                            (ZonedDateTime) argument);
                return builder.lessThan(propertyExpression,
                        argument.toString());

            case LESS_THAN_OR_EQUAL:
                if (argument instanceof ZonedDateTime)
                    return builder.lessThanOrEqualTo(propertyExpression.as(ZonedDateTime.class),
                            (ZonedDateTime) argument);
                return builder.lessThanOrEqualTo(propertyExpression,
                        argument.toString());
            case IN:
                return propertyExpression.in(args);
            case NOT_IN:
                return builder.not(propertyExpression.in(args));
        }

        return null;
    }

    private Boolean isDouble(String javaType) {
        return javaType.equals("class java.lang.Double");
    }

    // This method will help us diving deep into nested property using the dot convention
    // The originial tutorial did not have this, so it can only parse the shallow properties.
    private Path<String> parseProperty(Root<T> root) {
        Path<String> path;
        if (property.contains(".")) {
            // Nested properties
            String[] pathSteps = property.split("\\.");
            String step = pathSteps[0];
            path = root.get(step);

            for (int i = 1; i <= pathSteps.length - 1; i++) {
                path = path.get(pathSteps[i]);
            }
        } else {
            path = root.get(property);
        }
        return path;
    }

    private String formatArgument(Object argument) {
        String result = argument.toString();
        String HIBERNATE_ESCAPE_CHAR = "\\";
        return result
                .toLowerCase()
                .replace("\\", HIBERNATE_ESCAPE_CHAR + "\\")
                .replace("_", HIBERNATE_ESCAPE_CHAR + "_")
                .replace("%", HIBERNATE_ESCAPE_CHAR + "%")
                .replace('*', '%');
    }

    private List<Object> castArguments(Path<?> propertyExpression) {
        Class<?> type = propertyExpression.getJavaType();

        return arguments.stream().map(arg -> {
            if (type.equals(Integer.class)) return Integer.parseInt(arg);
            else if (type.equals(Long.class)) return Long.parseLong(arg);
            else if (type.equals(Byte.class)) return Byte.parseByte(arg);
            else return arg;
        }).collect(Collectors.toList());
    }
}
