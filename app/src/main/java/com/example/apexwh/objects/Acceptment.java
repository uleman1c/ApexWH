package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class Acceptment {

    public String ref, number, date, incomeNumber, incomeDate, sender, senderDescription, description, status, type, acceptStatus, contractor, contractorDescription,
            comment, manager, operation ;
    public Boolean overAccept, existPackPaper;

    //мОстатков.Добавить(Новый Структура("Дата,
    //	|ДатаВходящегоДокумента,
    //	|Номер,
    //	|НомерВходящегоДокумента,
    //	|Отправитель,
    //	|Распоряжение,
    //	|РаспоряжениеСтр,
    //	|ТипДокумента,
    //	|Состояние,
    //	|СостояниеПоступления,
    //	|ОтправительСтр,
    //	|Контрагент,
    //	|КонтрагентСтр,
    //	|Комментарий,
    //	|Менеджер, Перепоставка, ХозяйственнаяОперация, ЕстьУпакЛист",
    //	Формат(тсРаспоряженияНаОтгрузку.Дата, "ДФ=yyyyMMddHHmmss"),

    public Acceptment(String ref, String number, String date, String incomeNumber, String incomeDate, String sender, String senderDescription,
                      String description, String status, String type, String acceptStatus, String contractor, String contractorDescription,
                      String comment, String manager, String operation, Boolean overAccept, Boolean existPackPaper) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.incomeNumber = incomeNumber;
        this.incomeDate = incomeDate;
        this.sender = sender;
        this.senderDescription = senderDescription;
        this.description = description;
        this.status = status;
        this.type = type;
        this.acceptStatus = acceptStatus;
        this.contractor = contractor;
        this.contractorDescription = contractorDescription;
        this.comment = comment;
        this.manager = manager;
        this.operation = operation;
        this.overAccept = overAccept;
        this.existPackPaper = existPackPaper;
    }

    public static Acceptment FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "Распоряжение");
        String number = JsonProcs.getStringFromJSON(task_item, "Номер");
        String date = JsonProcs.getStringFromJSON(task_item, "Дата");
        String incomeNumber = JsonProcs.getStringFromJSON(task_item, "НомерВходящегоДокумента");
        String incomeDate = JsonProcs.getStringFromJSON(task_item, "ДатаВходящегоДокумента");
        String description = JsonProcs.getStringFromJSON(task_item, "РаспоряжениеСтр");
        String status = JsonProcs.getStringFromJSON(task_item, "Состояние");
        String type = JsonProcs.getStringFromJSON(task_item, "ТипДокумента");
        String acceptStatus = JsonProcs.getStringFromJSON(task_item, "СостояниеПоступления");
        String contractor = JsonProcs.getStringFromJSON(task_item, "Контрагент");
        String contractorDescription = JsonProcs.getStringFromJSON(task_item, "КонтрагентСтр");
        String sender = JsonProcs.getStringFromJSON(task_item, "Отправитель");
        String senderDescription = JsonProcs.getStringFromJSON(task_item, "ОтправительСтр");
        String comment = JsonProcs.getStringFromJSON(task_item, "Комментарий");
        String manager = JsonProcs.getStringFromJSON(task_item, "Менеджер");
        String operation = JsonProcs.getStringFromJSON(task_item, "ХозяйственнаяОперация");
        Boolean overAccept = JsonProcs.getBooleanFromJSON(task_item, "Перепоставка");
        Boolean existPackPaper = JsonProcs.getBooleanFromJSON(task_item, "ЕстьУпакЛист");

        return new Acceptment(ref, number, date, incomeNumber, incomeDate, sender, senderDescription,
                description, status, type, acceptStatus, contractor, contractorDescription, comment,
                manager, operation, overAccept, existPackPaper);


    }

}


