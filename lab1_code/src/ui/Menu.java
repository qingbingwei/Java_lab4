package ui;

import java.util.*;

/**
 * 菜单类
 * 管理菜单项和命令
 */
public class Menu {
    private String title;
    private Map<String, MenuItem> menuItems;
    private List<String> itemOrder;

    public Menu(String title) {
        this.title = title;
        this.menuItems = new LinkedHashMap<>();
        this.itemOrder = new ArrayList<>();
    }

    /**
     * 添加菜单项
     */
    public void addItem(String key, String description, MenuCommand command) {
        menuItems.put(key, new MenuItem(key, description, command));
        itemOrder.add(key);
    }

    /**
     * 显示菜单
     */
    public void display() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
        
        for (String key : itemOrder) {
            MenuItem item = menuItems.get(key);
            System.out.printf("  [%s] %s\n", item.getKey(), item.getDescription());
        }
        
        System.out.println("=".repeat(60));
    }

    /**
     * 执行菜单项
     */
    public boolean executeItem(String key) {
        MenuItem item = menuItems.get(key);
        if (item != null) {
            item.execute();
            return true;
        }
        return false;
    }

    /**
     * 菜单项内部类
     */
    private static class MenuItem {
        private String key;
        private String description;
        private MenuCommand command;

        public MenuItem(String key, String description, MenuCommand command) {
            this.key = key;
            this.description = description;
            this.command = command;
        }

        public String getKey() { return key; }
        public String getDescription() { return description; }
        public void execute() { command.execute(); }
    }
}
