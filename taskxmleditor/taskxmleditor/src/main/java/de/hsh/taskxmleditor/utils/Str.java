package de.hsh.taskxmleditor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Str {

    final static Logger log = LoggerFactory.getLogger(Str.class);
    static ResourceBundle bundle = PropertyResourceBundle.getBundle("messages");

    public static String get(String key) {
        try {
            String val = bundle.getString(key);
            if(null == val || val.isEmpty())
                return key;
            return val;
        } catch (Exception oops) {
            log.warn("language bundle does not contain this key: " + key);
            return key;
        }
    }

    public static String get(String key, Object... params) {
        try {
            String val = String.format(bundle.getString(key), params);
            if(null == val || val.isEmpty())
                return key;
            return val;
        } catch (Exception oops) {
            log.warn("language bundle does not contain this key: " + key);
            return key;
        }
    }
}
