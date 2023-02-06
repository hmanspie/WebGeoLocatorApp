package com.rebalcomb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "geo_data")
public class DocumentXLSXData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ip;
    private String port;
    private String time;
    private String status;
    private String authorization;
    private String servername;
    private String radioOff;
    private String hidden;
    private String bssid;
    private String essid;
    private String security;
    private String keyy;
    private String wps;
    private String lanIp;
    private String lanMask;
    private String wanIp;
    private String wanMask;
    private String wanGate;
    private String domain;
    private String code;
    private String country;
    private String region;
    private String city;
    private String latitude;
    private String longitude;
    private String zipcode;
    private String timezone;

}
