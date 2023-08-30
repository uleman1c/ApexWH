package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Income {

    public String date, number, receiver, order, orderDescription, orderType, status, shippingDate, contractor, contractorDescription, comment, manager, loadDate;
    public int pictureIndex, weight;
    public Boolean changed, shippingFromSave;

    public Income(String date, String number, String receiver, String order, String orderDescription, String orderType, String status,
                  String shippingDate, String contractor, String contractorDescription, String comment, String manager, String loadDate,
                  int pictureIndex, int weight, Boolean changed, Boolean shippingFromSave) {
        this.date = date;
        this.number = number;
        this.receiver = receiver;
        this.order = order;
        this.orderDescription = orderDescription;
        this.orderType = orderType;
        this.status = status;
        this.shippingDate = shippingDate;
        this.contractor = contractor;
        this.contractorDescription = contractorDescription;
        this.comment = comment;
        this.manager = manager;
        this.loadDate = loadDate;
        this.pictureIndex = pictureIndex;
        this.weight = weight;
        this.changed = changed;
        this.shippingFromSave = shippingFromSave;
    }

    public static Income FromJson(JSONObject task_item){

        //{"Дата":"20230623105645",
        // "ИндексКартинки":0,
        // "Номер":"ПЭТ00001929","Получатель":"Чураев Г. Ш. ИП","Распоряжение":"7b096143-119b-11ee-a999-000c29b5d947",
        // "РаспоряжениеСтр":"Заказ клиента ПЭ00-001633 от 23.06.2023 10:56:45",
        // ТипДокумента":"ЗаказКлиента",
        // "Состояние":"Ожидается отбор",
        //
        // "Изменен":false,
        //
        // "ПолучательСтр":"Чураев Г. Ш. ИП",
        //
        // "ДатаОтгрузки":"20230704000000","Контрагент":"9d2f46f6-1310-11ed-a996-000c29dbc4e3","КонтрагентСтр":"АВН ГРУПП ООО",
        // "Комментарий":"","Менеджер":"Городецкая Наталья",
        //
        // "Вес":1543,
        // "ОтгрузкаСОтветственногоХранения":false,
        //
        // "ДатаЗагрузки":"20230626095320"}

        String date = JsonProcs.getStringFromJSON(task_item, "Дата");
        int pictureIndex = JsonProcs.getIntegerFromJSON(task_item, "ИндексКартинки");
        String number = JsonProcs.getStringFromJSON(task_item, "Номер");
        String receiver = JsonProcs.getStringFromJSON(task_item, "Получатель");
        String order = JsonProcs.getStringFromJSON(task_item, "Распоряжение");
        String orderDescription = JsonProcs.getStringFromJSON(task_item, "РаспоряжениеСтр");
        String orderType = JsonProcs.getStringFromJSON(task_item, "ТипДокумента");
        String status = JsonProcs.getStringFromJSON(task_item, "Состояние");
        Boolean changed = JsonProcs.getBooleanFromJSON(task_item, "Изменен");

        String shippingDate = JsonProcs.getStringFromJSON(task_item, "ДатаОтгрузки");
        String contractor = JsonProcs.getStringFromJSON(task_item, "Контрагент");
        String contractorDescription = JsonProcs.getStringFromJSON(task_item, "КонтрагентСтр");
        String comment = JsonProcs.getStringFromJSON(task_item, "Комментарий");
        String manager = JsonProcs.getStringFromJSON(task_item, "Менеджер");
        int weight = JsonProcs.getIntegerFromJSON(task_item, "Вес");
        Boolean shippingFromSave = JsonProcs.getBooleanFromJSON(task_item, "ОтгрузкаСОтветственногоХранения");
        String loadDate = JsonProcs.getStringFromJSON(task_item, "ДатаЗагрузки");

        return new Income(date, number, receiver, order, orderDescription, orderType, status,
            shippingDate, contractor, contractorDescription, comment, manager, loadDate,
            pictureIndex, weight, changed, shippingFromSave);


    }


}
