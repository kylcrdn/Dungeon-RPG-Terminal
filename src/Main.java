/*
 * =====================================================================
 *  DUNGEON RPG - Mini project "Your text adventure"
 *  Authors: [Name1 Lastname1] and [Name2 Lastname2]
 * =====================================================================
 *
 *  PLANNING
 *
 *  1. What is the game about?
 *     The player enters a dungeon with 5 floors. Each floor has one enemy,
 *     and the 5th floor is the final boss. Between floors the player earns
 *     gold and can buy better weapons or potions in the shop.
 *
 *  2. How does a game go? What does the player decide?
 *     The player types a name and a class (warrior, mage or archer).
 *     On each floor there is a turn-based fight. After winning, the
 *     player gets random gold and decides what to buy in the shop:
 *     a weapon for their class, a healing potion, or nothing.
 *
 *  4. How do you win and lose?
 *     You win if you beat all 5 floors.
 *     You lose if your HP reaches 0 on any floor.
 *     In both cases you go back to the main menu.
 *
 *  TASK SPLIT
 *  Frances:  main menu, player creation, floors loop, gold, shop,
 *            game over / victory.
 *  [Partner]: combat (the fight() function and its helper functions).
 *
 *  Requirement             | Where we use it                          | Who
 *  ------------------------|------------------------------------------|---------
 *  1. int + double         | menu/shop options (int), [combat double] | Both
 *  2. text with equals()   | class name in askClass()                 | Frances
 *  3. validation           | askOption(), askName(), askClass(), shop | Frances
 *  4. switch               | main menu, shop, weapon names            | Frances
 *  5. if/else if/else      | startingHP(), startingDamage()           | Frances
 *  6. && and ||            | askClass() (&&), potion in shop (||)     | Frances
 *  7. nested if            | buying a weapon in the shop              | Frances
 *  8. for + while          | floors (for), menus and checks (while)   | Frances
 *  9. 4 functions          | see the functions below the main         | Both
 * 10. Random               | goldReward(), [combat damage]            | Both
 * =====================================================================
 */

import java.util.Random;
import java.util.Scanner;

public class Doydoy_Cardano {

    // Created once and shared by every function
    static Scanner sc = new Scanner(System.in);
    static Random r = new Random();

    // =================================================================
    // MAIN: the main menu. It repeats until the player chooses Quit.
    // =================================================================
    public static void main(String[] args) {
        int option;

        do {
            System.out.println();
            System.out.println("=================================");
            System.out.println("          DUNGEON RPG");
            System.out.println("=================================");
            System.out.println("1. Play");
            System.out.println("2. How to play");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");
            option = askOption(1, 3);

            switch (option) {
                case 1:
                    playGame();
                    break;
                case 2:
                    System.out.println("Beat the enemy on each of the 5 floors.");
                    System.out.println("Win gold after each fight and spend it in the shop.");
                    System.out.println("If your HP reaches 0, it's game over.");
                    break;
                case 3:
                    System.out.println("Thanks for playing. See you next time!");
                    break;
            }
        } while (option != 3);
    }

    // =================================================================
    // ONE FULL GAME: create the player, play 5 floors, then go back
    // to the main menu (after losing or after winning).
    // =================================================================
    static void playGame() {
        // ---- Player creation ----
        String name = askName();
        String playerClass = askClass();

        int maxHP = startingHP(playerClass);
        int playerHP = maxHP;
        int baseDamage = startingDamage(playerClass);
        int weaponLevel = 0;   // 0 = no weapon, 1-3 = weapon bought
        int weaponBonus = 0;   // extra damage from the weapon
        int gold = 0;

        System.out.println();
        System.out.println("Welcome, " + name + " the " + playerClass + "!");
        System.out.println("HP: " + playerHP + " | Damage: " + baseDamage);

        // ---- Floors loop: floors 1 to 5 ----
        for (int floor = 1; floor <= 5; floor++) {
            System.out.println();
            System.out.println("========== FLOOR " + floor + " ==========");

            // COMBAT (partner's part). It returns the HP left after the fight.
            playerHP = fight(floor, playerHP, baseDamage + weaponBonus, playerClass);

            // ---- Game over ----
            if (playerHP <= 0) {
                System.out.println();
                System.out.println("You have fallen on floor " + floor + "...");
                System.out.println("GAME OVER");
                return; // back to the main menu
            }

            // ---- Gold and shop (not after the final boss) ----
            if (floor < 5) {
                int reward = goldReward();
                gold = gold + reward;
                System.out.println("You found " + reward + " gold! Total: " + gold + " gold.");

                // Shop: repeats until the player chooses to continue
                int shopOption;
                do {
                    showShop(playerClass, gold, playerHP, maxHP, weaponLevel);
                    shopOption = askOption(1, 5);

                    switch (shopOption) {
                        case 1:
                        case 2:
                        case 3:
                            // Options 1-3 are the weapons, so the option is the weapon level
                            if (shopOption <= weaponLevel) {
                                System.out.println("You already have that weapon or a better one.");
                            } else {
                                int price = weaponPrice(shopOption);
                                if (gold >= price) {
                                    gold = gold - price;
                                    weaponLevel = shopOption;
                                    weaponBonus = weaponDamage(shopOption);
                                    System.out.println("You bought " + weaponName(playerClass, shopOption)
                                            + "! Damage: " + (baseDamage + weaponBonus));
                                } else {
                                    System.out.println("Not enough gold. You need " + price + " gold.");
                                }
                            }
                            break;
                        case 4:
                            // Healing potion
                            if (gold < 30 || playerHP == maxHP) {
                                System.out.println("You can't buy a potion (not enough gold or HP already full).");
                            } else {
                                gold = gold - 30;
                                playerHP = playerHP + 40;
                                if (playerHP > maxHP) {
                                    playerHP = maxHP;
                                }
                                System.out.println("You feel better. HP: " + playerHP + "/" + maxHP);
                            }
                            break;
                        case 5:
                            System.out.println("You climb the stairs to the next floor...");
                            break;
                    }
                } while (shopOption != 5);
            }
        }

        // ---- Victory: all 5 floors beaten ----
        System.out.println();
        System.out.println("*********************************************");
        System.out.println("   The dragon falls. The dungeon is silent.");
        System.out.println("   " + name + " the " + playerClass + ", you have conquered");
        System.out.println("   all five floors. Your legend begins today!");
        System.out.println("*********************************************");
    }

    // =================================================================
    // COMBAT - PARTNER'S PART
    // Receives the floor, the player's HP, the player's total damage and
    // the class. Must return the player's HP at the end of the fight
    // (0 or less = the player lost).
    // =================================================================
    static int fight(int floor, int playerHP, int damage, String playerClass) {
        // TODO (partner): create the enemy for this floor (switch on floor),
        // then the combat loop: while (playerHP > 0 && enemyHP > 0) { ... }

        // PLACEHOLDER so the game can be tested: the player always wins
        // and loses 10 HP. To test GAME OVER, change it to: return 0;
        System.out.println("[Placeholder] You fight the enemy with " + damage + " damage and win!");
        return playerHP - 10;
    }

    // =================================================================
    // INPUT FUNCTIONS (all of them validate what the player types)
    // =================================================================

    // Asks for a number between min and max and keeps asking until it is valid
    static int askOption(int min, int max) {
        int option = sc.nextInt();
        while (option < min || option > max) {
            System.out.print("That option doesn't exist. Choose " + min + "-" + max + ": ");
            option = sc.nextInt();
        }
        sc.nextLine(); // clears the Enter left by nextInt()
        return option;
    }

    // Asks for the player's name and does not accept an empty one
    static String askName() {
        System.out.print("Enter your hero's name: ");
        String name = sc.nextLine().trim();
        while (name.length() == 0) {
            System.out.print("The name can't be empty. Enter your hero's name: ");
            name = sc.nextLine().trim();
        }
        return name;
    }

    // Asks for the class as text and compares it with equals()
    static String askClass() {
        System.out.print("Choose your class (warrior / mage / archer): ");
        String playerClass = sc.nextLine().trim().toLowerCase();
        while (!playerClass.equals("warrior") && !playerClass.equals("mage")
                && !playerClass.equals("archer")) {
            System.out.print("That class doesn't exist. Type warrior, mage or archer: ");
            playerClass = sc.nextLine().trim().toLowerCase();
        }
        return playerClass;
    }

    // =================================================================
    // CLASS STATS
    // =================================================================

    // Starting HP of each class
    static int startingHP(String playerClass) {
        if (playerClass.equals("warrior")) {
            return 120;
        } else if (playerClass.equals("mage")) {
            return 90;
        } else {
            return 100; // archer
        }
    }

    // Starting damage of each class
    static int startingDamage(String playerClass) {
        if (playerClass.equals("warrior")) {
            return 15;
        } else if (playerClass.equals("mage")) {
            return 12; // the mage also has mana in combat
        } else {
            return 14; // archer
        }
    }

    // =================================================================
    // GOLD AND SHOP
    // =================================================================

    // Random gold after each win: from 50 to 100 (101 is not included)
    static int goldReward() {
        return r.nextInt(50, 101);
    }

    // Price of each weapon level
    static int weaponPrice(int level) {
        switch (level) {
            case 1:
                return 50;
            case 2:
                return 110;
            default:
                return 200;
        }
    }

    // Extra damage of each weapon level
    static int weaponDamage(int level) {
        switch (level) {
            case 1:
                return 10;
            case 2:
                return 15;
            default:
                return 30;
        }
    }

    // Name of the weapon, depending on the class and the level
    static String weaponName(String playerClass, int level) {
        if (playerClass.equals("warrior")) {
            switch (level) {
                case 1: return "Demon-Slayer Sword";
                case 2: return "Demon-Dweller Sword";
                default: return "Demon-Slasher Sword";
            }
        } else if (playerClass.equals("mage")) {
            switch (level) {
                case 1: return "Scepter";
                case 2: return "Elder Wand";
                default: return "Staff of Ainz Ooal Gown";
            }
        } else {
            switch (level) {
                case 1: return "Compound Bow";
                case 2: return "Erdtree Bow";
                default: return "Spirit of Euros";
            }
        }
    }

    // Shows the shop with the weapons of the player's class
    static void showShop(String playerClass, int gold, int playerHP, int maxHP, int weaponLevel) {
        System.out.println();
        System.out.println("------------- SHOP -------------");
        System.out.println("Gold: " + gold + " | HP: " + playerHP + "/" + maxHP);
        for (int level = 1; level <= 3; level++) {
            String owned = "";
            if (level == weaponLevel) {
                owned = "  (equipped)";
            }
            System.out.println(level + ". " + weaponName(playerClass, level)
                    + "  +" + weaponDamage(level) + " dmg  - " + weaponPrice(level) + " gold" + owned);
        }
        System.out.println("4. Healing potion  +40 HP  - 30 gold");
        System.out.println("5. Continue to the next floor");
        System.out.print("Choose: ");
    }
}