package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONObject;

public class OutcomeReceiver {

    public String type, ref, name;
    public OutcomeReceiver(String type, String ref, String name) {
        this.type = type;
        this.ref = ref;
        this.name = name;
    }

    public static OutcomeReceiver FromJson(JSONObject task_item){

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

        String type = JsonProcs.getStringFromJSON(task_item, "type");
        String ref = JsonProcs.getStringFromJSON(task_item, "Тиrefп");
        String name = JsonProcs.getStringFromJSON(task_item, "name");

        return new OutcomeReceiver(type, ref, name);


    }


}
