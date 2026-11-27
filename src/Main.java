/*
 * DUNGEON RPG
 * Authors: [Name1 Lastname1] and [Name2 Lastname2]
 *
 * 1. What is it about? Beat 5 floors of a dungeon, one enemy per floor.
 *    Win gold after each floor and buy weapons or potions in the shop.
 * 2. How is a game? Choose a name and a class, fight on each floor,
 *    then decide what to buy in the shop.
 * 4. Win: beat all 5 floors. Lose: HP reaches 0.
 */

import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Main {

    // Locale.US so decimals are typed with a dot (2.5)
    static Scanner sc = new Scanner(System.in).useLocale(Locale.US);
    static Random r = new Random();

    // Main menu: repeats until the player quits
    public static void main(String[] args) {
        int option;
        do {
            System.out.println("\n===== DUNGEON RPG =====");
            System.out.println("1. Play");
            System.out.println("2. How to play");
            System.out.println("3. Quit");
            System.out.print("Choose: ");
            option = askOption(1, 3);

            switch (option) {
                case 1:
                    playGame();
                    break;
                case 2:
                    System.out.println("Beat the enemy on all 5 floors.");
                    System.out.println("Spend your gold in the shop.");
                    System.out.println("If your HP reaches 0, you lose.");
                    break;
                case 3:
                    System.out.println("Bye!");
                    break;
            }
        } while (option != 3);
    }

    // One full game: create the player and play the 5 floors
    static void playGame() {
        String name = askName();
        String type = askClass();

        int maxHP = startHP(type);
        int hp = maxHP;
        int damage = startDamage(type);
        int weapon = 0; // 0 = no weapon, 1 to 3 = weapon level
        int gold = 0;

        System.out.println("Welcome, " + name + " the " + type + "!");

        // Floors loop
        for (int floor = 1; floor <= 5; floor++) {
            System.out.println("\n===== FLOOR " + floor + " =====");

            // Combat, returns the HP left
            int attack = damage + weaponDamage(weapon);
            hp = fight(floor, hp, maxHP, attack, type);

            // Game over
            if (hp <= 0) {
                System.out.println("You died on floor " + floor + ".");
                System.out.println("GAME OVER");
                return;
            }

            // No gold or shop after the boss
            if (floor == 5) {
                break;
            }

            // Gold reward
            int reward = r.nextInt(50, 101);
            gold = gold + reward;
            System.out.println("You found " + reward + " gold!");

            // Shop: repeats until the player continues
            int choice;
            do {
                System.out.println("\n--- SHOP ---");
                System.out.println("Gold: " + gold);
                System.out.println("HP: " + hp + "/" + maxHP);
                for (int i = 1; i <= 3; i++) {
                    System.out.print(i + ". " + weaponName(type, i));
                    System.out.print(" +" + weaponDamage(i) + " dmg");
                    System.out.print(" - " + weaponPrice(i) + "g");
                    // Shows which weapon the player is using
                    if (i == weapon) {
                        System.out.print(" (EQUIPPED)");
                    }
                    System.out.println();
                }
                System.out.println("4. Potion +40 HP - 30g");
                System.out.println("5. Next floor");
                System.out.print("Choose: ");
                choice = askOption(1, 5);

                // Buy a weapon
                if (choice <= 3) {
                    if (choice <= weapon) {
                        System.out.println("You already have a better weapon.");
                    } else if (gold < weaponPrice(choice)) {
                        System.out.println("Not enough gold.");
                    } else {
                        gold = gold - weaponPrice(choice);
                        weapon = choice;
                        String w = weaponName(type, choice);
                        System.out.println("You bought " + w + "!");
                        System.out.println(w + " is now equipped.");
                    }
                }

                // Buy a potion
                if (choice == 4) {
                    if (gold < 30 || hp == maxHP) {
                        System.out.println("Not enough gold or HP is full.");
                    } else {
                        gold = gold - 30;
                        hp = hp + 40;
                        if (hp > maxHP) {
                            hp = maxHP;
                        }
                        System.out.println("HP: " + hp + "/" + maxHP);
                    }
                }
            } while (choice != 5);
        }

        // Victory
        System.out.println("\n*************************************");
        System.out.println("  Lucifer falls. Silence returns.");
        System.out.println("  " + name + ", you conquered all 5 floors!");
        System.out.println("  Your legend begins today.");
        System.out.println("*************************************");
    }

    // COMBAT: one fight on one floor. Returns the player's HP at the end
    // (0 = the player died).
    static int fight(int floor, int hp, int maxHP, int attack, String type) {
        // Enemy of this floor
        String enemy = "Slime";
        String special = "Slime Splash";
        int enemyHP = 30;
        int enemyAttack = 5;
        int interval = 4; // special attack every X turns

        switch (floor) {
            case 2:
                enemy = "Goblin";
                special = "Savage Strike";
                enemyHP = 45;
                enemyAttack = 8;
                interval = 3;
                break;
            case 3:
                enemy = "Skeleton";
                special = "Bone Crusher";
                enemyHP = 60;
                enemyAttack = 12;
                interval = 4;
                break;
            case 4:
                enemy = "Orc";
                special = "Crushing Blow";
                enemyHP = 80;
                enemyAttack = 15;
                interval = 3;
                break;
            case 5:
                enemy = "Lucifer, Keeper of Hell";
                special = "Hellfire";
                enemyHP = 120;
                enemyAttack = 20;
                interval = 3;
                break;
        }

        int enemyMaxHP = enemyHP;
        int potions = 2; // potions for this fight
        int bonus = 0;   // extra damage after defending
        int turn = 1;
        boolean phaseTwo = false;

        System.out.println("A wild " + enemy + " appears!");

        // Combat loop: until someone reaches 0 HP
        while (hp > 0 && enemyHP > 0) {

            // Lucifer gets stronger below half HP
            if (floor == 5 && enemyHP <= 60 && !phaseTwo) {
                phaseTwo = true;
                enemyAttack = enemyAttack + 5;
                interval = 2;
                System.out.println("\nLucifer enters Phase 2!");
            }

            // Damage the enemy will deal this turn
            int incoming = enemyAttack;
            if (turn % interval == 0) {
                incoming = incoming * 2;
                System.out.println("\nWARNING: " + special + " incoming!");
            }

            System.out.println("\n--- TURN " + turn + " ---");
            healthBar("You", hp, maxHP);
            healthBar(enemy, enemyHP, enemyMaxHP);
            System.out.println("Potions: " + potions);
            System.out.println("1. Attack 2. Defend 3. Heal 4. Heavy Attack");
            System.out.print("Choose: ");
            int option = askOption(1, 4);
            int dealt = 0;

            switch (option) {
                case 1:
                    // Normal attack: random damage, chance of critical
                    dealt = attack + r.nextInt(0, 6) + bonus;
                    bonus = 0;
                    if (isCritical(type)) {
                        dealt = dealt * 2;
                        System.out.println("CRITICAL HIT!");
                    }
                    break;
                case 2:
                    // Defend: half damage, +5 on the next attack
                    incoming = incoming / 2;
                    bonus = 5;
                    System.out.println("You defend! Next attack +5.");
                    break;
                case 3:
                    // Heal with a potion
                    if (potions == 0 || hp == maxHP) {
                        System.out.println("You can't heal now!");
                        continue; // choose again, the enemy waits
                    }
                    potions--;
                    hp = hp + 30;
                    if (hp > maxHP) {
                        hp = maxHP;
                    }
                    System.out.println("You heal. HP: " + hp);
                    break;
                case 4:
                    // Heavy attack: the player chooses the power (decimal)
                    // More power = more damage but less chance to hit
                    System.out.print("Power (1.5 - 3.0): ");
                    double power = askDecimal(1.5, 3.0);
                    int chance = 100 - (int) (power * 25);
                    if (r.nextInt(1, 101) <= chance) {
                        dealt = (int) (attack * power) + bonus;
                        System.out.println("HEAVY ATTACK HITS!");
                    } else {
                        System.out.println("Heavy attack missed!");
                    }
                    bonus = 0;
                    break;
            }

            // Apply the player's damage
            if (dealt > 0) {
                enemyHP = enemyHP - dealt;
                if (enemyHP < 0) {
                    enemyHP = 0;
                }
                System.out.println("You dealt " + dealt + " damage.");
            }

            // Enemy attacks back if it is still alive
            if (enemyHP > 0) {
                hp = hp - incoming;
                if (hp < 0) {
                    hp = 0;
                }
                System.out.println(enemy + " dealt " + incoming + " damage.");
            }
            turn++;
        }

        if (hp > 0) {
            System.out.println("\nYou defeated " + enemy + "!");
        }
        return hp;
    }

    // Prints a health bar of 20 characters
    static void healthBar(String name, int hp, int maxHP) {
        System.out.print("[");
        for (int i = 0; i < 20; i++) {
            if (i * maxHP < hp * 20) {
                System.out.print("#");
            } else {
                System.out.print("-");
            }
        }
        System.out.println("] " + name + " " + hp + "/" + maxHP);
    }

    // Critical hit: archers 25%, the other classes 15%
    static boolean isCritical(String type) {
        int roll = r.nextInt(1, 101);
        if (type.equals("archer")) {
            return roll <= 25;
        }
        return roll <= 15;
    }

    // Asks for a decimal between min and max until it is valid
    static double askDecimal(double min, double max) {
        double value = sc.nextDouble();
        while (value < min || value > max) {
            System.out.print("Invalid. Choose " + min + " - " + max + ": ");
            value = sc.nextDouble();
        }
        sc.nextLine();
        return value;
    }

    // Asks for a number between min and max until it is valid
    static int askOption(int min, int max) {
        int option = sc.nextInt();
        while (option < min || option > max) {
            System.out.print("Invalid. Choose " + min + "-" + max + ": ");
            option = sc.nextInt();
        }
        sc.nextLine(); // clears the Enter
        return option;
    }

    // Asks for a name that is not empty
    static String askName() {
        System.out.print("Hero name: ");
        String name = sc.nextLine().trim();
        while (name.length() == 0) {
            System.out.print("Name can't be empty: ");
            name = sc.nextLine().trim();
        }
        return name;
    }

    // Asks for the class and checks it with equals()
    static String askClass() {
        System.out.print("Class (warrior / mage / archer): ");
        String type = sc.nextLine().trim().toLowerCase();
        while (!type.equals("warrior") && !type.equals("mage")
                && !type.equals("archer")) {
            System.out.print("Invalid. Type warrior, mage or archer: ");
            type = sc.nextLine().trim().toLowerCase();
        }
        return type;
    }

    // Starting HP of each class
    static int startHP(String type) {
        if (type.equals("warrior")) {
            return 120;
        } else if (type.equals("mage")) {
            return 90;
        } else {
            return 100;
        }
    }

    // Starting damage of each class
    static int startDamage(String type) {
        if (type.equals("warrior")) {
            return 15;
        } else if (type.equals("mage")) {
            return 17;
        } else {
            return 14;
        }
    }

    // Price of each weapon level
    static int weaponPrice(int level) {
        switch (level) {
            case 1: return 50;
            case 2: return 110;
            default: return 200;
        }
    }

    // Extra damage of each weapon level (0 = no weapon)
    static int weaponDamage(int level) {
        switch (level) {
            case 1: return 10;
            case 2: return 15;
            case 3: return 30;
            default: return 0;
        }
    }

    // Weapon name for each class and level
    static String weaponName(String type, int level) {
        if (type.equals("warrior")) {
            switch (level) {
                case 1: return "Demon-Slayer Sword";
                case 2: return "Demon-Dweller Sword";
                default: return "Demon-Slasher Sword";
            }
        } else if (type.equals("mage")) {
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
}