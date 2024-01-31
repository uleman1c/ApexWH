package com.example.apexwh.objects;

import com.example.apexwh.JsonProcs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContainerWithContent {

    public Container container;
    public ArrayList<ProductWithQuantity> productWithQuantities;

    public ContainerWithContent(Container container, ArrayList<ProductWithQuantity> productWithQuantities) {
        this.container = container;
        this.productWithQuantities = productWithQuantities;
    }

    public static ContainerWithContent FromJson(JSONObject task_item) {

        Container container = Container.FromJson(JsonProcs.getJsonObjectFromJsonObject(task_item, "container"));

        ArrayList<ProductWithQuantity> productWithQuantities = new ArrayList<>();

        JSONArray ja = JsonProcs.getJsonArrayFromJsonObject(task_item, "products");

        for (int i = 0; i < ja.length(); i++) {

            JSONObject jo = JsonProcs.getItemJSONArray(ja, i);

            productWithQuantities.add(ProductWithQuantity.FromJson(jo));

        }

        return new ContainerWithContent(container, productWithQuantities);


    }


}
