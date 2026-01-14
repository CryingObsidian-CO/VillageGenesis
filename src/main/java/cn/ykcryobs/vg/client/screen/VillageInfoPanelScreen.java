package cn.ykcryobs.vg.client.screen;

import cn.ykcryobs.vg.networking.payload.VillageInfoRequestPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 村庄信息面板屏幕 显示村庄基本信息的GUI界面
 *
 * @author llykff
 */
@OnlyIn(Dist.CLIENT)
public class VillageInfoPanelScreen extends Screen {

    private final String villageId;
    private final List<StringWidget> infoWidget;
    private int textX, textY, textWidth, textHeight;


    public VillageInfoPanelScreen(Component title, String villageId) {
        super(title);
        this.villageId = villageId;
        infoWidget = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void init() {
        super.init();

        this.createResponsiveLayout();
    }

    private void createResponsiveLayout() {
        int screenWidth = this.width;
        int screenHeight = this.height;

        // 左侧区域设置（按钮列表区域）
        int leftPanelWidth = (int) (screenWidth * 0.25); // 左侧占屏幕宽度的25%
        int leftPanelX = 10;
        int leftPanelY = 25;
        int leftPanelHeight = screenHeight - 50;

        // 右侧区域设置（显示文本区域）
        int rightPanelWidth = (int) (screenWidth * 0.7); // 右侧占屏幕宽度的65%
        int rightPanelX = leftPanelX + leftPanelWidth + (int) (screenWidth * 0.05); // 左侧面板右边+20像素间距
        int rightPanelY = 40;
        int rightPanelHeight = screenHeight - 70;

        // 创建按钮列表
        createResponsiveButtonList(leftPanelX, leftPanelY, leftPanelWidth, leftPanelHeight);

        // 创建只读文本显示框
        createTextDisplay(rightPanelX, rightPanelY, rightPanelWidth, rightPanelHeight);

        // 创建关闭按钮（底部居中）
        createCloseButton(screenWidth, screenHeight);
    }

    private void createResponsiveButtonList(int x, int y, int width, int height) {
        // 计算响应式按钮尺寸
        int buttonHeight = Math.max(25,
                Math.min(height / (VillageInfoCategory.values().length + 2), 40)); // 根据可用高度动态计算按钮高度
        int buttonWidth = Math.min(width - 20, 200); // 按钮宽度
        int buttonSpacing = 2; // 按钮间距

        // 从上到下排列按钮
        for (int i = 0; i < VillageInfoCategory.values().length; i++) {
            VillageInfoCategory category = VillageInfoCategory.values()[i];
            Component categoryName = category.getCategoryName();
            int buttonY = y + i * (buttonHeight + buttonSpacing);

            // 确保按钮不超出面板底部
            if (buttonY + buttonHeight > y + height) {
                break;
            }

            Button button = Button.builder(categoryName, clickedButton -> onCategoryButtonClick(category))
                    .bounds(x, buttonY, buttonWidth, buttonHeight)
                    .tooltip(Tooltip.create(category.getCategoryTooltip())).build();

            this.addRenderableWidget(button);
        }
    }

    private void createCloseButton(int screenWidth, int screenHeight) {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonX = (screenWidth - buttonWidth) / 2; // 居中
        int buttonY = screenHeight - 25; // 距离底部25像素

        Button closeButton = Button.builder(Component.translatable("gui.village_genesis.close"),
                button -> this.onClose()).bounds(buttonX, buttonY, buttonWidth, buttonHeight).build();

        this.addRenderableWidget(closeButton);
    }

    private void createTextDisplay(int x, int y, int width, int height) {
        this.textX = x;
        this.textY = y;
        this.textWidth = width;
        this.textHeight = height;
        onCategoryButtonClick(VillageInfoCategory.BASIC_INFO);
    }

    private void onCategoryButtonClick(VillageInfoCategory category) {
        PacketDistributor.sendToServer(new VillageInfoRequestPayload(category.name(), this.villageId));
        displayLoadingInfo();
    }

    private void displayLoadingInfo() {
        clearInfoWidget();

        infoWidget.add(new StringWidget(this.textX, this.textY, this.textWidth, 16,
                Component.translatable("gui.village_genesis.loading"), this.font));
        refreshDisplayInfo();

    }

    public void refreshDisplayInfo() {
        for (StringWidget stringWidget : infoWidget) {
            this.addRenderableWidget(stringWidget);
        }
    }

    public void clearInfoWidget() {
        for (StringWidget widget : infoWidget) {
            this.removeWidget(widget);
        }
        infoWidget.clear();
    }

    public void addInfo(Component component) {
        infoWidget.add(new StringWidget(this.textX, this.textY + infoWidget.size() * 16, this.textWidth, 16,
                component, this.font));
    }

    public enum VillageInfoCategory {

        BASIC_INFO("basic_info"),

        VILLAGER_LIST("villager_list"),

        FACILITY_LIST("facility_list"),

        ECONOMY_STATUS("economy_status"),

        DEFENSE_FACILITIES("defense_facilities"),

        JOB_DISTRIBUTION("job_distribution"),

        REPUTATION("reputation"),

        TRADE_RELATIONSA("trade_relations"),

        TRADE_RELATIONSQ("trade_relations"),

        TRADE_RELATIONS("trade_relations");

        private final String category;

        VillageInfoCategory(String category) {
            this.category = category;
        }

        public String getCategory() {
            return category;
        }

        public Component getCategoryName() {
            return Component.translatable("gui.village_genesis.info_category." + category);
        }

        public Component getCategoryTooltip() {
            return Component.translatable("gui.village_genesis.info_category.tooltip." + category);
        }
    }
}
