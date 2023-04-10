package cz.sli0095.promod.utils;



import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
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
        if(s == null){
            return longs;
        }
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
