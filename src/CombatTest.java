
import java.util.Scanner;

public class CombatTest {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int playerHP = 100;
        int playerAttack = 15;
        int potions = 3;
        int room = 1;

        int totalDamage = 0;
        int totalDamageTaken = 0;
        int totalTurns = 0;

        System.out.println("=== DUNGEON RPG ===");

        while (room <= 5 && playerHP > 0) {

            // START OF COMBAT SYSTEM

            String enemyName = "Slime";
            String specialAttack = "";

            int enemyHP = 30;
            int enemyAttack = 5;
            int attackInterval = 0;

            // Generate enemy depending on the level
            switch (room) {
                case 2:
                    enemyName = "Goblin";
                    enemyHP = 45;
                    enemyAttack = 8;
                    specialAttack = "Savage Strike";
                    attackInterval = 3;
                    break;

                case 3:
                    enemyName = "Skeleton";
                    enemyHP = 60;
                    enemyAttack = 12;
                    specialAttack = "Bone Crusher";
                    attackInterval = 4;
                    break;

                case 4:
                    enemyName = "Orc";
                    enemyHP = 80;
                    enemyAttack = 15;
                    specialAttack = "Crushing Blow";
                    attackInterval = 3;
                    break;

                case 5:
                    enemyName = "Lucifer, Keeper of Hell";
                    enemyHP = 120;
                    enemyAttack = 20;
                    specialAttack = "Hellfire";
                    attackInterval = 3;
                    break;
            }

            int enemyMaxHP = enemyHP;
            int turn = 1;
            int attackBonus = 0;

            boolean bossPhaseTwo = false;

            System.out.println("\n=== LEVEL " + room
                    + ": " + enemyName + " ===");

            // COMBAT LOOP
            while (playerHP > 0 && enemyHP > 0) {

                // Lucifer's second phase
                if (room == 5 && enemyHP <= 60 && !bossPhaseTwo) {
                    bossPhaseTwo = true;
                    enemyAttack += 5;
                    attackInterval = 2;

                    System.out.println("\nLucifer enters Phase 2!");
                }

                // Calculate incoming enemy damage
                int incomingDamage = enemyAttack;

                if (attackInterval > 0
                        && turn % attackInterval == 0) {

                    incomingDamage *= 2;

                    System.out.println("\nWARNING: "
                            + specialAttack + " incoming!");
                }

                // Display combat statistics
                System.out.println("\n--- TURN " + turn + " ---");

                // Player health bar
                System.out.print("Player HP: [");

                for (int i = 0; i < 20; i++) {
                    if (i * 100 < playerHP * 20) {
                        System.out.print("#");
                    } else {
                        System.out.print("-");
                    }
                }

                System.out.println("] " + playerHP + "/100");

                // Enemy health bar
                System.out.print("Enemy HP:  [");

                for (int i = 0; i < 20; i++) {
                    if (i * enemyMaxHP < enemyHP * 20) {
                        System.out.print("#");
                    } else {
                        System.out.print("-");
                    }
                }

                System.out.println("] " + enemyHP + "/" + enemyMaxHP);

                System.out.println("Attack: " + playerAttack
                        + " | Potions: " + potions);

                System.out.println("Attack bonus: +" + attackBonus);
                System.out.println("Incoming damage: " + incomingDamage);

                // Combat menu
                System.out.print("\n1. Attack"
                        + "\n2. Defend"
                        + "\n3. Heal"
                        + "\n4. Heavy Attack"
                        + "\nChoose: ");

                // Input validation
                if (!scan.hasNextInt()) {

                    if (!scan.hasNext()) {
                        scan.close();
                        return;
                    }

                    scan.next();

                    System.out.println(
                            "Please enter a number from 1 to 4.");

                    continue;
                }

                int option = scan.nextInt();
                int damage = 0;

                // Player actions
                switch (option) {

                    case 1:
                        // Normal attack with random damage
                        damage = playerAttack
                                + (int) (Math.random() * 6);

                        // 15% critical hit chance
                        if (Math.random() < 0.15) {
                            damage *= 2;

                            System.out.println("CRITICAL HIT!");
                        }

                        damage += attackBonus;
                        attackBonus = 0;
                        break;

                    case 2:
                        // Reduce incoming damage by 50%
                        incomingDamage /= 2;

                        // Bonus damage for next attack
                        attackBonus = 5;

                        System.out.println("You defended!");
                        System.out.println("Next attack: +5 damage.");
                        break;

                    case 3:
                        // Check if healing is possible
                        if (potions <= 0 || playerHP >= 100) {
                            System.out.println("Cannot heal!");
                            continue;
                        }

                        int previousHP = playerHP;

                        playerHP += 30;
                        potions--;

                        // Prevent overhealing
                        if (playerHP > 100) {
                            playerHP = 100;
                        }

                        System.out.println("Recovered "
                                + (playerHP - previousHP) + " HP.");
                        break;

                    case 4:
                        // Heavy attack: 10% success chance
                        if (Math.random() < 0.10) {

                            damage = playerAttack * 2 + attackBonus;

                            System.out.println(
                                    "HEAVY ATTACK SUCCESSFUL!");

                        } else {
                            System.out.println("Heavy attack missed!");
                        }

                        attackBonus = 0;
                        break;

                    default:
                        System.out.println("Pick the correct option.");
                        continue;
                }

                // Apply player damage
                if (damage > 0) {

                    // Prevent negative enemy health
                    if (damage > enemyHP) {
                        damage = enemyHP;
                    }

                    enemyHP -= damage;
                    totalDamage += damage;

                    System.out.println("You dealt "
                            + damage + " damage.");
                }

                // Enemy counterattack
                if (enemyHP > 0) {

                    // Prevent negative player health
                    if (incomingDamage > playerHP) {
                        incomingDamage = playerHP;
                    }

                    playerHP -= incomingDamage;
                    totalDamageTaken += incomingDamage;

                    System.out.println(enemyName + " dealt "
                            + incomingDamage + " damage.");
                }

                turn++;
                totalTurns++;
            }

            // END OF COMBAT SYSTEM

            // Temporary level progression
            // Replace with teammate's dungeon and shop system
            if (playerHP > 0) {

                System.out.println("\nYou defeated "
                        + enemyName + "!");

                if (room < 5) {

                    playerAttack += 5;
                    playerHP += 20;
                    potions++;

                    if (playerHP > 100) {
                        playerHP = 100;
                    }

                    System.out.println("Level cleared!");
                    System.out.println("Rewards:");
                    System.out.println("+5 Attack");
                    System.out.println("Up to 20 HP restored");
                    System.out.println("+1 Potion");
                }

                room++;
            }
        }

        // FINAL RESULTS
        System.out.println("\n============================");

        if (playerHP <= 0) {
            System.out.println("GAME OVER!");
            System.out.println("Defeated on level " + room);
        } else {
            System.out.println("VICTORY!");
            System.out.println("Lucifer has fallen!");
            System.out.println("You cleared the dungeon!");
        }

        System.out.println("============================");

        System.out.println("Levels cleared: " + (room - 1) + "/5");
        System.out.println("Total damage dealt: " + totalDamage);
        System.out.println("Total damage received: " + totalDamageTaken);
        System.out.println("Total turns: " + totalTurns);
        System.out.println("Remaining HP: " + playerHP);
        System.out.println("Remaining potions: " + potions);

        scan.close();
    }
}
