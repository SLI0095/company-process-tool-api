package com.semestral_project.company_process_tool.utils;



import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.util.ArrayList;
import java.util.List;

@Convert
public class LongListConverter implements AttributeConverter<List<Long>, String> {

    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<Long> longs) {
        if(longs == null || longs.isEmpty()){
            return "";
        }
        return StringUtils.join(longs,SPLIT_CHAR);
    }

    @Override
    public List<Long> convertToEntityAttribute(String s) {
        List<Long> longs = new ArrayList<>();
        for(String value : s.split(SPLIT_CHAR)){
            try{
                long number = Long.parseLong(value);
                longs.add(number);
            } catch (NumberFormatException ignored){
            }
        }
        return longs;
    }
}
