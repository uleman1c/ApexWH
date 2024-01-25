package com.example.apexwh.objects;

import com.example.apexwh.R;

public class NavigationNames {

    public static int getIdFromName(String name){

        int result = 0;

        switch (name){

            case "Acceptment": result = R.id.nav_acceptmentFragment; break;
            case "Shipment": result = R.id.shipmentFragment; break;
            case "Returns": result = R.id.nav_returns; break;
            case "Tests": result = R.id.nav_tests; break;
            case "ReturnsOfProducts": result = R.id.nav_returnsOfProductsFragment; break;
            case "OrdersToChangeCharacteristic": result = R.id.nav_ordersToChangeCharacteristicFragment; break;
            case "Placement": result = R.id.nav_placementMenuFragment; break;
            case "Takement": result = R.id.nav_takementMenuFragment; break;
            case "Collect": result = R.id.nav_collectListFragment; break;
            case "Movement": result = R.id.nav_movementFragment; break;
            case "Moves": result = R.id.movementListFragment; break;
            case "CellContent": result = R.id.nav_cellContentListFragment; break;
            case "ContainerContent": result = R.id.nav_containerContentListFragment; break;
            case "ProductCells": result = R.id.nav_productCellsListFragment; break;
            case "Invents": result = R.id.nav_inventarizations; break;
            case "InventTasks": result = R.id.inventTasksFragment; break;
            case "RefillTasks": result = R.id.refillTasksFragment; break;
            case "ShtrihcodeProduct": result = R.id.nav_shtrihcodeProductFragment; break;
            case "ShtrihcodeContainer": result = R.id.nav_shtrihcodeContainerFragment; break;

        }

        return result;

    }

}
