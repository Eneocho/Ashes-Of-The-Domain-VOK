package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui;

import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover.CommodityInfo;
import data.kaysaar.aotd.vok.scripts.SoundUIManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class GPMegasturcutreMenu implements CustomUIPanelPlugin, SoundUIManager {
    public CustomPanelAPI panel;
    public ArrayList<ButtonAPI> buttonsOfMegastructures;
    public GPManager manager;
    public CustomPanelAPI panelOfMarketData;
    public CustomPanelAPI currentMegastructureSectionsPanel;
    public CustomPanelAPI currentMegastructureSelected;
    public CustomPanelAPI totalCostOfMegastructuresPanel;
    public static float staticWidthOfMegaButtons = 450;
    public static float totalCostHeight = 100f;
    public GPIndividualMegastructreMenu currentOne;
    public GPBaseMegastructure megastructure;
    public TooltipMakerAPI buttonTooltipMaker;
    float spacerX = 0f; //Used for left panels

    public void createMegastructureList() {
        if(buttonsOfMegastructures==null){
            buttonsOfMegastructures = new ArrayList<>();
        }
        buttonsOfMegastructures.clear();
        UILinesRenderer renderer = new UILinesRenderer(0f);
        currentMegastructureSectionsPanel = panel.createCustomPanel(staticWidthOfMegaButtons, panel.getPosition().getHeight() - 51 , renderer);
        renderer.setPanel(currentMegastructureSectionsPanel);
        TooltipMakerAPI tooltip = currentMegastructureSectionsPanel.createUIElement(currentMegastructureSectionsPanel.getPosition().getWidth(), currentMegastructureSectionsPanel.getPosition().getHeight(), false);
        tooltip.addSectionHeading("Megastructures", Alignment.MID, 0f);
        TooltipMakerAPI buttonTooltip = currentMegastructureSectionsPanel.createUIElement(currentMegastructureSectionsPanel.getPosition().getWidth(), currentMegastructureSectionsPanel.getPosition().getHeight() - 20, true);
        float pad = 0f;
        for (GPBaseMegastructure megastructure : GPManager.getInstance().getMegastructures()) {
            placeButton(megastructure, buttonTooltip, pad);
            pad = 5f;

        }



        buttonTooltipMaker = buttonTooltip;
        currentMegastructureSectionsPanel.addUIElement(tooltip).inTL(0, 0);
        currentMegastructureSectionsPanel.addUIElement(buttonTooltip).inTL(0, 20);
        panel.addComponent(currentMegastructureSectionsPanel).inTL(spacerX, 51);

    }

    private void placeButton(GPBaseMegastructure baseMegastructure, TooltipMakerAPI buttonTooltip, float pad) {
        CustomPanelAPI panelAPI = baseMegastructure.createButtonSection(staticWidthOfMegaButtons - 7);
        ButtonAPI buttonAPI = buttonTooltip.addAreaCheckbox("", baseMegastructure, NidavelirMainPanelPlugin.base, NidavelirMainPanelPlugin.bg, NidavelirMainPanelPlugin.bright, staticWidthOfMegaButtons - 7, panelAPI.getPosition().getHeight(), pad);
        buttonsOfMegastructures.add(buttonAPI);
        buttonTooltip.addCustom(panelAPI, 0f).getPosition().inTL(buttonAPI.getPosition().getX(), -buttonAPI.getPosition().getY() - buttonAPI.getPosition().getHeight());
        buttonTooltip.setHeightSoFar(Math.abs(buttonAPI.getPosition().getY()));
    }

    public void createCurrentMegastructureTab() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        currentMegastructureSelected = panel.createCustomPanel(panel.getPosition().getWidth() - staticWidthOfMegaButtons - 10, panel.getPosition().getHeight() - 51, renderer);
        renderer.setPanel(currentMegastructureSelected);
        TooltipMakerAPI tooltip = currentMegastructureSelected.createUIElement(currentMegastructureSelected.getPosition().getWidth(), currentMegastructureSelected.getPosition().getHeight(), true);
        String str = "";
        if (megastructure != null) {
            str += megastructure.getSpec().getName();
            if(megastructure.getEntityTiedTo()!=null){
                str+=" : "+megastructure.getEntityTiedTo().getStarSystem().getName();
            }
        }
        tooltip.addSectionHeading(str, Alignment.MID, 0f);
        if(megastructure!=null){
            currentOne = megastructure.createUIPlugin(currentMegastructureSelected,this);
            currentOne.initUI();
            tooltip.addCustom(currentOne.getMainPanel(), 0f);
            tooltip.setHeightSoFar(currentOne.getMainPanel().getPosition().getHeight());
        }
        currentMegastructureSelected.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(currentMegastructureSelected).inTL(spacerX + staticWidthOfMegaButtons + 5, 51);


    }

    public void createMarketResourcesPanel() {
        float width = UIData.WIDTH / 2;
        panelOfMarketData = panel.createCustomPanel(width, 50, null);
        TooltipMakerAPI tooltip = panelOfMarketData.createUIElement(width, 50, false);
        float totalSize = width;
        float sections = totalSize / commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = 35;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getTotalResources().entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
            tooltip.addTooltipToPrevious(new CommodityInfo(entry.getKey(), 700, true, false, manager.getProductionOrders()), TooltipMakerAPI.TooltipLocation.BELOW);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x, topYImage);
            String text = "" + entry.getValue();
            String text2 = text + "(" + GPManager.getInstance().getReqResources(GPManager.getInstance().getProductionOrders()).get(entry.getKey()) + ")";
            tooltip.addPara("" + entry.getValue() + " %s", 0f, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, "(" + manager.getExpectedCostsFromManager().get(entry.getKey()) + ")").getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            x += sections;
        }
        panelOfMarketData.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(panelOfMarketData).inTL(5 + width / 2, 5);
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {
        if(buttonsOfMegastructures!=null){
            for (ButtonAPI buttonsOfMegastructure : buttonsOfMegastructures) {
                if(buttonsOfMegastructure.isChecked()){
                    buttonsOfMegastructure.setChecked(false);
                    if(!buttonsOfMegastructure.getCustomData().equals(megastructure)){
                        megastructure = (GPBaseMegastructure) buttonsOfMegastructure.getCustomData();
                        panel.removeComponent(currentMegastructureSelected);
                        createCurrentMegastructureTab();
                    }

                    reInitalizeButtonUI();
                    break;
                }
            }
        }
    }

    public void reInitalizeButtonUI(){
        buttonsOfMegastructures.clear();
        float currentOffset = buttonTooltipMaker.getExternalScroller().getYOffset();
        panel.removeComponent(currentMegastructureSectionsPanel);
        panel.removeComponent(totalCostOfMegastructuresPanel);
        buttonTooltipMaker=null;
        createMegastructureList();
        buttonTooltipMaker.getExternalScroller().setYOffset(currentOffset);
    }
    public void init(CustomPanelAPI mainPanel) {
        this.panel = mainPanel;
        this.manager = GPManager.getInstance();
        createMarketResourcesPanel();
        createMegastructureList();
        createCurrentMegastructureTab();
    }

    public CustomPanelAPI getMainPanel() {
        return panel;
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    @Override
    public void playSound() {

    }

    @Override
    public void pauseSound() {

    }
    public void resetMarketData(){
        panel.removeComponent(panelOfMarketData);
        createMarketResourcesPanel();
    }
    public void clearUI() {
        if(currentOne!=null) {
            currentOne.clearUI();
        }
        buttonsOfMegastructures.clear();
        panel.removeComponent(panelOfMarketData);
        panel.removeComponent(currentMegastructureSectionsPanel);
        panel.removeComponent(currentMegastructureSelected);
        panel.removeComponent(totalCostOfMegastructuresPanel);
    }
}
