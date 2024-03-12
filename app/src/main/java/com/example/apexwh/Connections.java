package com.example.apexwh;

import java.util.HashMap;
import java.util.Map;

public class Connections {

    private static final String mainAddr = "http://duck.titan.loc/"; // https://ow.apx-service.ru/

    //public static String addr = "http://10.0.2.2/apex_tech_man";
    public static String addr = mainAddr + "tech_man";

    public static String user= "exch";

    public static String password = "123456";


    public static String fileAddr = mainAddr + "apos/upload/uploadmp";

    public static String fileAccessKey = "6a64edd9-56f8-11ed-8a49-bc305bf806da";

    public static String addrApx = "https://apx-srv.ru:8002/upload";



    public static String addrMob = mainAddr + "tech_man/hs/mob/";
    public static String addrDta = mainAddr + "tech_man/hs/dta/obj/";
    public static String addrDtaLoc = "http://192.168.182.238:8000/whdct";
    public static String addrFiles = mainAddr + "tech_man/hs/dta/files/";

    public static Map<String, String> headers() {

        HashMap headers = new HashMap<String, String>();

        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Basic ZXhjaDoxMjM0NTY=");


        return headers;

    };




}


