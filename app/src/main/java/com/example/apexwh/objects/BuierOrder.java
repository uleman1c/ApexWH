package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class BuierOrder {

    public String ref, name, nameStr, number, date, description, reciever, outcomeDate, comment;


    public BuierOrder(String ref, String name, String nameStr, String number, String date, String description, String reciever,
                      String outcomeDate, String comment) {
        this.ref = ref;
        this.name = name;
        this.nameStr = nameStr;
        this.number = number;
        this.date = date;
        this.description = description;
        this.reciever = reciever;
        this.outcomeDate = outcomeDate;
        this.comment = comment;
    }

////            Дата,
//            |ИндексКартинки,
////            |Номер,
//            |Получатель,
////            |Распоряжение,
////            |РаспоряжениеСтр,
////            |ТипДокумента,
//            |Состояние,
//            |Изменен,
////            |ПолучательСтр,
////            |ДатаОтгрузки,
//            |Контрагент,
//            |КонтрагентСтр,
////            |Комментарий,
//            |Менеджер,
//            |Вес,
//            |ОтгрузкаСОтветственногоХранения,
//            |ДатаЗагрузки




    public static BuierOrder FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "Распоряжение");
        String name = JsonProcs.getStringFromJSON(task_item, "ТипДокумента");
        String nameStr = JsonProcs.getStringFromJSON(task_item, "РаспоряжениеСтр");
        String number = JsonProcs.getStringFromJSON(task_item, "Номер");
        String date = JsonProcs.getStringFromJSON(task_item, "Дата");
        String description = JsonProcs.getStringFromJSON(task_item, "РаспоряжениеСтр");
        String reciever = JsonProcs.getStringFromJSON(task_item, "ПолучательСтр");
        String outcomeDate = JsonProcs.getStringFromJSON(task_item, "ДатаЗагрузки");
        String comment = JsonProcs.getStringFromJSON(task_item, "Комментарий");

        date = date.substring(6, 8) + "." + date.substring(4, 6) + "." + date.substring(0, 4);

        return new BuierOrder(ref, name, nameStr, number, date, description, reciever, outcomeDate, comment);


    }

}


