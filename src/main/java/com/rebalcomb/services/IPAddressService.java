package com.rebalcomb.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Service
public class IPAddressService {

    public long ipToLong(String ipAddress) {
        String[] ipAddressInArray = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {
            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ip * Math.pow(256, power);
        }
        return result;
    }

    public String longToIp(long ip) {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));
            if (i < 3) {
                sb.insert(0, '.');
            }
            ip = ip >> 8;
        }
        return sb.toString();
    }

    public List<String> findIpAddress(String inputText){
        return Pattern.compile("(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)")
                .matcher(inputText)
                .results()
                .map(MatchResult::group)
                .toList();
    }

}
