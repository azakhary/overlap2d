/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package com.uwsoft.editor.mvc.view.ui.properties.panels;

import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.puremvc.patterns.observer.Notification;
import com.uwsoft.editor.gdx.sandbox.Sandbox;
import com.uwsoft.editor.mvc.Overlap2DFacade;
import com.uwsoft.editor.mvc.view.ui.properties.UIAbstractProperties;
import com.uwsoft.editor.mvc.view.ui.properties.UIItemPropertiesMediator;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.factory.EntityFactory;
import com.uwsoft.editor.utils.runtime.ComponentCloner;
import com.uwsoft.editor.utils.runtime.ComponentRetriever;

/**
 * Created by azakhary on 4/15/2015.
 */
public class UIBasicItemPropertiesMediator extends UIItemPropertiesMediator<Entity, UIBasicItemProperties> {
    private static final String TAG = UIBasicItemPropertiesMediator.class.getCanonicalName();
    public static final String NAME = TAG;

    private TransformComponent transformComponent;
    private MainItemComponent mainItemComponent;
    private DimensionsComponent dimensionComponent;
    private TintComponent tintComponent;

    private HashMap<String, UIBasicItemProperties.ItemType> itemTypeMap = new HashMap<>();

    public UIBasicItemPropertiesMediator() {
        super(NAME, new UIBasicItemProperties());
    }

    @Override
    public void onRegister() {
        itemTypeMap.put("ENTITY_"+EntityFactory.COMPOSITE_TYPE, UIBasicItemProperties.ItemType.composite);
        itemTypeMap.put("ENTITY_"+EntityFactory.IMAGE_TYPE, UIBasicItemProperties.ItemType.texture);
        itemTypeMap.put("ENTITY_"+EntityFactory.PARTICLE_TYPE, UIBasicItemProperties.ItemType.particle);
        itemTypeMap.put("ENTITY_"+EntityFactory.LABEL_TYPE, UIBasicItemProperties.ItemType.text);
        itemTypeMap.put("ENTITY_"+EntityFactory.SPRITE_TYPE, UIBasicItemProperties.ItemType.spriteAnimation);
        itemTypeMap.put("ENTITY_"+EntityFactory.SPRITER_TYPE, UIBasicItemProperties.ItemType.spriterAnimation);
        itemTypeMap.put("ENTITY_"+EntityFactory.SPINE_TYPE, UIBasicItemProperties.ItemType.spineAnimation);
        itemTypeMap.put("ENTITY_"+EntityFactory.LIGHT_TYPE, UIBasicItemProperties.ItemType.light);
    }

    @Override
    public String[] listNotificationInterests() {
        String[] defaultNotifications = super.listNotificationInterests();
        String[] notificationInterests = new String[]{
                UIBasicItemProperties.TINT_COLOR_BUTTON_CLICKED,
        };

        return ArrayUtils.addAll(defaultNotifications, notificationInterests);
    }

    @Override
    public void handleNotification(Notification notification) {
        super.handleNotification(notification);

        switch (notification.getName()) {
            case UIBasicItemProperties.TINT_COLOR_BUTTON_CLICKED:
                ColorPicker picker = new ColorPicker(new ColorPickerAdapter() {
                    @Override
                    public void finished(Color newColor) {
                        viewComponent.setTintColor(newColor);
                        facade.sendNotification(viewComponent.getUpdateEventName());
                    }
                });

                picker.setColor(viewComponent.getTintColor());
                Sandbox.getInstance().getUIStage().addActor(picker.fadeIn());

                break;
            default:
                break;
        }
    }

    protected void translateObservableDataToView(Entity entity) {
    	transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
    	mainItemComponent = ComponentRetriever.get(entity, MainItemComponent.class);
    	dimensionComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
    	tintComponent = ComponentRetriever.get(entity, TintComponent.class);

        viewComponent.setItemType(itemTypeMap.get("ENTITY_"+entity.flags));
        viewComponent.setIdBoxValue(mainItemComponent.itemIdentifier);
        viewComponent.setXValue(transformComponent.x + "");
        viewComponent.setYValue(transformComponent.y + "");
        //TODO no flip anymore
        //viewComponent.setFlipH(vo.isFlipedH);
        //viewComponent.setFlipV(vo.isFlipedV);
        
        viewComponent.setWidthValue(dimensionComponent.width + "");
        viewComponent.setHeightValue(dimensionComponent.height + "");
        viewComponent.setRotationValue(transformComponent.rotation + "");
        viewComponent.setScaleXValue(transformComponent.scaleX + "");
        viewComponent.setScaleYValue(transformComponent.scaleY + "");
        viewComponent.setTintColor(tintComponent.color);
    }

    @Override
    protected void translateViewToItemData() {
        //MainItemVO vo = observableReference.getDataVO();
    	Entity entity  = ((Entity) observableReference);

        transformComponent = ComponentCloner.get(ComponentRetriever.get(entity, TransformComponent.class));
        mainItemComponent = ComponentCloner.get(ComponentRetriever.get(entity, MainItemComponent.class));
        dimensionComponent = ComponentCloner.get(ComponentRetriever.get(entity, DimensionsComponent.class));
        tintComponent = ComponentCloner.get(ComponentRetriever.get(entity, TintComponent.class));

    	mainItemComponent.itemIdentifier = viewComponent.getIdBoxValue();
    	transformComponent.x = NumberUtils.toFloat(viewComponent.getXValue(), transformComponent.x);
    	transformComponent.y = NumberUtils.toFloat(viewComponent.getYValue(), transformComponent.y);

        dimensionComponent.width = NumberUtils.toFloat(viewComponent.getWidthValue());
        dimensionComponent.height = NumberUtils.toFloat(viewComponent.getHeightValue());
    	
    	//TODO nor more flip
    	//vo.isFlipedH = viewComponent.getFlipH();
    	//vo.isFlipedV = viewComponent.getFlipV();
    	
        // TODO: manage width and height
        transformComponent.rotation = NumberUtils.toFloat(viewComponent.getRotationValue(), transformComponent.rotation);
    	transformComponent.scaleX = (viewComponent.getFlipH() ? -1 : 1) * NumberUtils.toFloat(viewComponent.getScaleXValue(), transformComponent.scaleX);
    	transformComponent.scaleY = (viewComponent.getFlipV() ? -1 : 1) * NumberUtils.toFloat(viewComponent.getScaleYValue(), transformComponent.scaleY);
        Color color = viewComponent.getTintColor();
        tintComponent.color.set(color);

        Array<Component> componentsToUpdate = new Array<>();
        componentsToUpdate.add(transformComponent);
        componentsToUpdate.add(mainItemComponent);
        componentsToUpdate.add(dimensionComponent);
        componentsToUpdate.add(tintComponent);
        Object[] payload = new Object[2];
        payload[0] = entity;
        payload[1] = componentsToUpdate;
        Overlap2DFacade.getInstance().sendNotification(Sandbox.ACTION_UPDATE_ITEM_DATA, payload);
    }
}
