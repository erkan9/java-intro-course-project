import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Erkan Kamber
 *
 */

public class MonopolyGame {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        Random randomNumGenerator = new Random();

        File investBox = new File("resources/invest_box");

        FileReader investBoxReader = new FileReader(investBox);

        BufferedReader bufferedReader = new BufferedReader(investBoxReader);

        byte placedBoxesCounter = 1;
        byte diceWall = 2;

        char firstPlayerStealPower = 'a';
        char secondPlayerStealPower = 'a';
        char currentPlayersPower = 'a';
        String winner = "";

        int playerId = 0;
        int rowedDice = 0;
        int indexToBePlaced = 0;
        int firstPlayerPosition;
        int secondPlayerPostion;
        int currentPlayerPosition = 0;
        int currentPlayerMoney = 0;

        int[] stealBoxCoordinates = new int[3];

        int investedMoney = 0;

        int firstPlayerMoney = 1000;
        int secondPlayerMoney = 1000;

        // ArrayLists that contains the information about invested money and the coefficient of the company
        ArrayList<Integer> allInvestedMoney = new ArrayList<>();
        ArrayList<Integer> coefficientOfCompanies = new ArrayList<>();

        boolean isGameOver = false;
        boolean isFirstRound = true;

        boolean isFirstPlanActivated;
        boolean isSecondPlanActivated;

        // Booleans created to check if both of the players are before\at the START
        boolean isFirstPlayerFinished;
        boolean isSecondPlayerFinished;
        boolean currentPlan;

        //variables created to check if the steal boxes are activated
        boolean isFirstStealBoxAtive;
        boolean isMidStealBoxAtive;
        boolean isLastStealBoxAtive;

        //variables created to check if the players are at the end of the board
        boolean isPlayerOneFinished;
        boolean isPlayerTwoFinished;
        boolean isCurrentPlayerFinished;

        final byte ARRAY_SIZE = 20;
        final byte NUMBER_OF_I_P_C_ST_BOXES = 3;

        String[] monopolyBoard;

      /*  String[][] allCompanies = {
                {"Evel Co", "500", "0.2", "-5", "100"},          //line 0
                {"Bombs Away", "400", "0.5", "-10", "50"},          //line 1
                {"Clock Work Orange", "300", "1.5", "-15", "35"},          //line 2
                {"Maroders unated", "200", "2.0", "-18", "50"},          //line 3
                {"Fatcat incorporated", "100", "2.5", "-25", "100"},         //line 4
                {"Macrosoft", "50", "5.0", "-20", "10"}           //line 5

        };

       */

        String[][] allCompanies = new String[6][5];

        allCompanies = investBoxReader(investBox, investBoxReader, bufferedReader);

        //example(example);

        while (!isGameOver) {

            boxAssignmentDisplay();

            monopolyBoard = boardSetter();

            allBoxesPlacerOnBoard(monopolyBoard, placedBoxesCounter, randomNumGenerator, indexToBePlaced, ARRAY_SIZE, NUMBER_OF_I_P_C_ST_BOXES);

            firstPlayerStealPower = whichPlanWillYouEnable(randomNumGenerator);

            secondPlayerStealPower = whichPlanWillYouEnable(randomNumGenerator);

            stealBoxCoordinates = stealBoxCoordinatesFinder(stealBoxCoordinates, monopolyBoard);

            if (isFirstRound) {

                playerId = whoWillStartFirst(randomNumGenerator);

            } else {

                if (firstPlayerMoney > secondPlayerMoney) {

                    playerId = 0;
                } else {

                    playerId = 1;
                }
            }

            firstPlayerPosition = 0;
            secondPlayerPostion = 0;

            isFirstPlanActivated = false;
            isSecondPlanActivated = false;

            isFirstPlayerFinished = false;
            isSecondPlayerFinished = false;

            currentPlan = false;
            isFirstRound = false;

            isFirstStealBoxAtive = true;
            isMidStealBoxAtive = true;
            isLastStealBoxAtive = true;

            isPlayerOneFinished = false;
            isPlayerTwoFinished = false;
            isCurrentPlayerFinished = false;

            while (!isPlayerOneFinished && !isPlayerTwoFinished) {

                boardDisplay(monopolyBoard);

                rowedDice = rollTheDice(diceWall, randomNumGenerator);

                if (playerId % 2 == 0) {

                    if (!isPlayerOneFinished) {

                        System.out.println("-----First Player's Turn-----");

                        firstPlayerPosition += rowedDice;

                        currentPlayerPosition = firstPlayerPosition;

                        currentPlayerMoney = firstPlayerMoney;

                        currentPlayersPower = firstPlayerStealPower;

                        currentPlan = isFirstPlanActivated;
                    }
                } else {

                    if (!isPlayerTwoFinished) {

                        System.out.println("----------Second Player's Turn----------");

                        secondPlayerPostion += rowedDice;

                        currentPlayerPosition = secondPlayerPostion;

                        currentPlayerMoney = secondPlayerMoney;

                        currentPlayersPower = secondPlayerStealPower;

                        currentPlan = isSecondPlanActivated;
                    }
                }

                if (monopolyBoard[currentPlayerPosition].equalsIgnoreCase("|C|")) {

                    System.out.println("*Време е да хвърлим зарчетно на късмета*");

                    currentPlayerMoney = whatIsYourChance(randomNumGenerator, currentPlayerMoney);

                    if (currentPlan) {

                        if (currentPlayersPower == 'C') {

                            System.out.println("Откраднахте 100 парички [+100]");

                            currentPlayerMoney += 100;
                        }
                    }

                } else if (monopolyBoard[currentPlayerPosition].equalsIgnoreCase("|P|")) {

                    System.out.println("*Азис ще взима участие в 'Bushido', много голям фен съм му, каня те с мен.*");

                    currentPlayerMoney = partyHardBox(currentPlayerMoney);


                } else if (monopolyBoard[currentPlayerPosition].equalsIgnoreCase("|I|")) {

                    System.out.println("*Време е за инвестиция.*");

                    currentPlayerMoney = investToCompany(scanner, randomNumGenerator, allCompanies,
                            currentPlayerMoney, allInvestedMoney, coefficientOfCompanies);

                    investedMoney = investCalculator(randomNumGenerator, allInvestedMoney, coefficientOfCompanies, allCompanies);

                    if (currentPlan) {

                        if (currentPlayersPower == 'I') {

                            System.out.println("Откраднахте 100 парички [+100]");

                            currentPlayerMoney += 100;
                        }
                    }

                } else if (monopolyBoard[currentPlayerPosition].equalsIgnoreCase("|T|")) {

                    System.out.println("Вие попаднахте в TRAP кутийка");
                    System.out.println("Губите 100 парички");

                    currentPlayerMoney -= 100;

                } else if (monopolyBoard[currentPlayerPosition].equalsIgnoreCase("|S|")) {

                    System.out.println("Вие попаднахте в STEAL кутийка");

                    if (currentPlayerPosition == stealBoxCoordinates[0]) {

                        if (isFirstStealBoxAtive) {

                            System.out.println("Вашият план се активира ");

                            System.out.printf("Steal кутия[%d] вече е деактивирана\n", stealBoxCoordinates[0]);

                            currentPlan = true;

                            isFirstStealBoxAtive = false;
                        }

                    } else if (currentPlayerPosition == stealBoxCoordinates[1]) {

                        if (isMidStealBoxAtive) {

                            System.out.println("Вашият план се активира ");

                            System.out.printf("Steal кутия[%d] вече е деактивирана\n", stealBoxCoordinates[1]);

                            currentPlan = true;

                            isMidStealBoxAtive = false;
                        }

                    } else if (currentPlayerPosition == stealBoxCoordinates[2]) {

                        if (isLastStealBoxAtive) {

                            System.out.println("Вашият план се активира ");

                            System.out.printf("Steal кутия[%d] вече е деактивирана\n", stealBoxCoordinates[2]);

                            currentPlan = true;

                            isLastStealBoxAtive = false;
                        }
                    }

                    if (currentPlan) {

                        if (currentPlayersPower == 'S') {

                            System.out.println("Откраднахте 100 парички [+100]");

                            currentPlayerMoney += 100;
                        }
                    }
                }

                if (currentPlayerPosition >= 19 || currentPlayerPosition == 0) {

                    if (playerId % 2 == 0) {

                        isPlayerOneFinished = true;
                    } else {

                        isPlayerTwoFinished = true;
                    }
                    currentPlayerPosition = 0;
                }

                if (playerId % 2 == 0) {

                    firstPlayerPosition = currentPlayerPosition;

                    firstPlayerMoney = currentPlayerMoney;

                    firstPlayerStealPower = currentPlayersPower;

                    isFirstPlanActivated = currentPlan;

                    winner = "Second";

                } else {

                    secondPlayerPostion = currentPlayerPosition;

                    secondPlayerMoney = currentPlayerMoney;

                    secondPlayerStealPower = currentPlayersPower;

                    isSecondPlanActivated = currentPlan;

                    winner = "First";
                }

                resultDisplayer(firstPlayerPosition, secondPlayerPostion, firstPlayerMoney, secondPlayerMoney);

                playerId++;

                if (currentPlayerMoney <= 0) {

                    isGameOver = true;

                    break;
                }
            }
        }
        System.out.printf("\n%s player is the winner", winner);
    }

    /**
     * Method that sets/resets the board
     * @return the 1D array that is the board
     */
    public static String[] boardSetter() {

        String[] monopolyBoard = {"|*|", "|X|", "|X|", "|X|", "|X|",
                "|X|", "|X|", "|X|", "|X|", "|X|",
                "|X|", "|X|", "|X|", "|X|", "|X|",
                "|X|", "|X|", "|X|", "|X|", "|X|"};

        return monopolyBoard;
    }


    /**
     * Method created to display the result
     * @param firstPlayerPosition first player's position
     * @param secondPlayerPostion second player's position
     * @param firstPlayerMoney first player's money
     * @param secondPlayerMoney second player's money
     */
    public static void resultDisplayer(int firstPlayerPosition, int secondPlayerPostion, int firstPlayerMoney, int secondPlayerMoney) {

        System.out.printf("| First's position is: [%s] | Second's position is: [%s] |\n ", firstPlayerPosition, secondPlayerPostion);
        System.out.printf("First's money : [%d] | Second's money: [%d]\n\n", firstPlayerMoney, secondPlayerMoney);

    }

    /**
     * Method that finds the coordinates of the STEAL boxes
     *
     * @param stealBoxCoordinates 1D array that contains the coordinates of all steal boxes
     * @param monopolyBoard       The monopoly board
     * @return 1D with all of the coordinates of the Steal boxes
     */
    public static int[] stealBoxCoordinatesFinder(int[] stealBoxCoordinates, String[] monopolyBoard) {

        int number = 0;

        for (int counter = 0; counter < monopolyBoard.length; counter++) {

            if (monopolyBoard[counter].equalsIgnoreCase("|S|")) {

                stealBoxCoordinates[number] = counter;

                number++;
            }
        }
        return stealBoxCoordinates;
    }

    /**
     * Method that enables the type of plan
     *
     * @param numGenerator The number generator
     * @return The character of the plan
     */
    public static char whichPlanWillYouEnable(Random numGenerator) {

        int generatedNumber = throwDiceForSteal(numGenerator);
        char character = 'a';

        switch (generatedNumber) {
            case 1:
                character = 'C';
                break;
            case 2:
                character = 'S';
                break;
            case 3:
                character = 'I';
        }
        return character;
    }

    /**
     * Method that throws a dice for the steal box
     *
     * @param numGenerator The generator
     * @return The number of the plan\
     */
    public static int throwDiceForSteal(Random numGenerator) {

        int generatedNum = numGenerator.nextInt(3) + 1;

        return generatedNum;
    }

    /**
     * Method that rolls a dice for invest box
     *
     * @param numGenerator The number generator
     * @param minValie     The min value of a company
     * @param maxValue     The max value of a company
     * @return the rolled number
     */
    public static int diceThrowForInvest(Random numGenerator, int minValie, int maxValue) {

        int generatedNumber = numGenerator.nextInt(maxValue + minValie * -1) + 1;

        return generatedNumber + minValie;
    }

    /**
     * Method that calculates if the current player wins the money or loses from invests
     *
     * @param generator              The number generator
     * @param allInvestedMoney       All invested money of current player
     * @param coefficientOfCompanies The coefficient of cmpanies
     * @param allCompanies           2D array that contaions all of the information about companies
     * @return The sum all invested money with earned money or lost money
     */
    public static int investCalculator(Random generator, ArrayList<Integer> allInvestedMoney,
                                       ArrayList<Integer> coefficientOfCompanies, String[][] allCompanies) {

        int sum = 0;
        int dice = 0;

        for (int counter = 0; counter < allInvestedMoney.size(); counter++) {

            int investedMoney = allInvestedMoney.get(counter);

            int minValue = Integer.parseInt(allCompanies[coefficientOfCompanies.get(counter)][3]);

            int maxValue = Integer.parseInt(allCompanies[coefficientOfCompanies.get(counter)][4]);

            double coefficientOfCompany = Double.parseDouble(allCompanies[coefficientOfCompanies.get(counter)][2]);

            dice = diceThrowForInvest(generator, minValue, maxValue);

            if (dice >= 0) {

                sum += coefficientOfCompany * investedMoney;

            }
        }
        return sum;
    }

    /**
     * Method that displays the randomly generated companies
     *
     * @param random The generator
     * @return Array that contains the number of generated companies
     */
    public static int[] companiesToBeDisplayed(Random random) {

        int[] generatedCompanies = new int[3];

        for (int i = 0; i <= 2; i++) {

            int generatedNumber = random.nextInt(6) + 1;

            if (generatedNumber == 1) {
                System.out.println("[0]Evel Co | min: 500 | risk/reward: 0.2");

                generatedCompanies[i] = 0;

            } else if (generatedNumber == 2) {
                System.out.println("[1]Bombs Away | min: 400 | risk/reward: 0.5");

                generatedCompanies[i] = 1;

            } else if (generatedNumber == 3) {
                System.out.println("[2]Clock Work Orange | min: 300 | risk/reward: 1.5");

                generatedCompanies[i] = 2;

            } else if (generatedNumber == 4) {
                System.out.println("[3]Maroders Unated | min: 200 | risk/reward: 2.0");

                generatedCompanies[i] = 3;


            } else if (generatedNumber == 6) {
                System.out.println("[5]Macrosoft | min: 50 | risk/reward: 5.0");

                generatedCompanies[i] = 5;


            } else if (generatedNumber == 5) {
                System.out.println("[4]Fatcat incorporated | min: 100 | risk/reward: 2.5");

                generatedCompanies[i] = 4;

            }
        }
        System.out.println("[N]Не искам да инвестирам");

        return generatedCompanies;
    }

    /**
     * Method created to calculate the invested money
     *
     * @param scanner                The scanner
     * @param random                 The generator
     * @param allCompanies           The companies and information about them
     * @param currentMoney           The current player's money
     * @param allInvestedMoney       All invested money to a company
     * @param coefficientOfCompanies The coefficient of a company
     */
    public static int investToCompany(Scanner scanner, Random random, String[][] allCompanies,
                                      int currentMoney, ArrayList<Integer> allInvestedMoney, ArrayList<Integer> coefficientOfCompanies) {

        int[] generatedCompanies = companiesToBeDisplayed(random);

        int min = 0;
        int optionNumber = 0;
        int moneyInvested;

        System.out.println("Инвестирайте разумно и изберете компания:");
        String chosenCompany = scanner.nextLine();

        while (!chosenCompany.equalsIgnoreCase("N")) {

            optionNumber = Integer.parseInt(chosenCompany);

            for (int companyNo = 0; companyNo < 3; companyNo++) {

                if (optionNumber == generatedCompanies[companyNo]) {

                    min = Integer.parseInt(allCompanies[optionNumber][1]);

                    System.out.println("Колко пари искате да инвестирате в тази фирма ?");

                    moneyInvested = Integer.parseInt(scanner.nextLine());

                    if (moneyInvested >= min && (currentMoney - moneyInvested >= 0)) {

                        currentMoney -= moneyInvested;

                        allInvestedMoney.add(moneyInvested);

                        coefficientOfCompanies.add(optionNumber);

                        break;

                    } else {
                        System.out.println("Неуспешна инвестиция");
                    }
                }
            }
            System.out.println("Инвестирайте разумно и изберете компания:");

            chosenCompany = scanner.nextLine();

            if (chosenCompany.equalsIgnoreCase("N")) {
                break;
            }
        }
        return currentMoney;
    }

    /**
     * Method that is used when the current player's position is equals to Party Hard box
     *
     * @param currentMoney Current player's money
     * @return The current player's money with taken money
     */
    public static int partyHardBox(int currentMoney) {

        currentMoney -= 25;

        System.out.println("*Твоята сметка: 25 шоколадови парички*");

        return currentMoney;
    }

    /**
     * Method that displays the unlucky options and adds money to current player's money
     *
     * @param generatedNumber Randomly generated number
     * @param currentMoney    The money of the current play(The one's turn)
     * @return the current money of the player with taken money
     */
    public static int unluckyOptions(int generatedNumber, int currentMoney) {

        if (generatedNumber >= 1 && generatedNumber <= 39) {

            System.out.println("----------1001 нощ----------");
            System.out.println("След купон установявате, че телевизорът Ви е откраднат.. [-50]");

            currentMoney -= 50;

        } else if (generatedNumber >= 40 && generatedNumber <= 65) {

            System.out.println("----------Балът на феите----------");
            System.out.println("Вие сте баща на три абитуриентки, бъдете готови за стабилни разходи. [-100]");

            currentMoney -= 100;

        } else if (generatedNumber >= 66 && generatedNumber <= 79) {

            System.out.println("----------Война и мир----------");
            System.out.println("Най - добрият Ви персонал получава повикване за казармата. Губите порсонал. [-150]");

            currentMoney -= 150;

        } else if (generatedNumber >= 80 && generatedNumber <= 94) {

            System.out.println("----------Престъпление и наказание----------");
            System.out.println("Налитат Ви на бой, отвръщате им и се озовавате в 3-то РПУ, губите времето, отделено за бизнес срещи. [-200]");

            currentMoney -= 200;

        } else {

            System.out.println("----------Гроздовете на гнева----------");
            System.out.println("Част от бизнесите Ви фалират, заради задаваща се епидемия. [-250]");
            System.out.println("Губите 250 парички");

            currentMoney -= 250;
        }

        return currentMoney;
    }

    /**
     * Method that displays the lucky options and adds money to current player's money
     *
     * @param generatedNumber Randomly generated number
     * @param currentMoney    The money of the current play(The one's turn)
     * @return the current money of the player with added money
     */
    public static int luckyOptions(int generatedNumber, int currentMoney) {

        if (generatedNumber >= 1 && generatedNumber <= 39) {

            System.out.println("----------Големите надежди----------");
            System.out.println("Осиновявате група сирачета, за да си вдигнете социалното реноме. [+50]");

            currentMoney += 50;

        } else if (generatedNumber >= 40 && generatedNumber <= 65) {

            System.out.println("----------Лолита----------");
            System.out.println("Хващате си гадже и получавате вечното уважение на батковците с големите рЪки. [+100]");

            currentMoney += 100;

        } else if (generatedNumber >= 66 && generatedNumber <= 79) {

            System.out.println("----------Гордост и предразсъдъци----------");
            System.out.println("Напускате университета и ставате милионер. Следващия Зукърбърг! [+150]");

            currentMoney += 150;

        } else if (generatedNumber >= 80 && generatedNumber <= 94) {

            System.out.println("----------Повелителя на мухите----------");
            System.out.println("Тийнейджъри представят идея за рационализиране на производствените мощности. [+200]");

            currentMoney += 200;

        } else {

            System.out.println("----------Хобит----------");
            System.out.println("Наемате джудже за личен асистент. [+250]");

            currentMoney += 250;
        }

        return currentMoney;
    }

    /**
     * Method that generates a random number from 1 to 100 to find the option
     *
     * @param generator The Random number generator
     * @return The randomly generated number for the options
     */
    public static int diceRollForChanceBox(Random generator) {

        int numberForOptions = 0;

        numberForOptions = generator.nextInt(100) + 1;

        return numberForOptions;
    }

    /**
     * The Main method for Chance box that calls other methods
     *
     * @param generator    The Random number generator
     * @param currentMoney Plays
     * @return Current player's money
     */
    public static int whatIsYourChance(Random generator, int currentMoney) {

        int rolledNumberForChance = diceRollForChanceBox(generator);

        boolean areYouLucky = areYouLucky(generator);

        if (areYouLucky) {

            System.out.println("Шансът Ви ще е с положителна последица. ");

            currentMoney = luckyOptions(rolledNumberForChance, currentMoney);

            return currentMoney;

        } else {

            System.out.println("*Шансът Ви ще е с отрицателна последица*");

            currentMoney = unluckyOptions(rolledNumberForChance, currentMoney);
        }
        return currentMoney;
    }

    /**
     * Method that generates a random number to find if the player is lucky or unlucly
     *
     * @param generator The Random number generator
     * @return boolean to check if he is luck or not
     */
    public static boolean areYouLucky(Random generator) {

        boolean areYouLucky = false;

        int number = 0;

        number = generator.nextInt(10) + 1;

        if (number % 2 == 0) {

            areYouLucky = true;
        }

        System.out.printf("Числото, което определя вашият шанс е: [%d]\n", number);

        return areYouLucky;
    }


    /**
     * Method created to generates a random number to check who will start first
     *
     * @param generator Random number generator
     * @return player's number id
     */
    public static int whoWillStartFirst(Random generator) {

        int playerId = 1;

        playerId = generator.nextInt(10) + 1;

        if (playerId % 2 == 0) {

            playerId = 0;

            return playerId;
        }

        return playerId;
    }

    /**
     * Method that displays the assignments of the boxes
     */
    public static void boxAssignmentDisplay() {

        System.out.println("Assignment of the boxes: \n" +
                "|*| - Start Box\n" +
                "|T| - Trap Box - [7] \n" +
                "|C| - Chance Box - [3] \n" +
                "|I| - Invest Box  - [3] \n" +
                "|P| - Party Hard Box - [3] \n" +
                "|S| - Steal Box - [3] \n");
    }

    /**
     * Method that rolls the dice
     *
     * @param diceWall          The number of walls of the dice
     * @param movementGenerator How many boxes will the player move
     * @return the number of boxes to move
     */
    public static int rollTheDice(byte diceWall, Random movementGenerator) {

        int movement = 0;

        movement = movementGenerator.nextInt(diceWall) + 1;

        return movement;
    }

    /**
     * Method that displays the board used during the game
     *
     * @param monopolyBoard The 1D array that contains all of the boxes
     */
    public static void boardDisplay(String[] monopolyBoard) {

        System.out.println(monopolyBoard[10] + monopolyBoard[11] + monopolyBoard[12] + monopolyBoard[13] + monopolyBoard[14] + monopolyBoard[15] + monopolyBoard[16] + monopolyBoard[17]);
        System.out.println(monopolyBoard[9] + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + monopolyBoard[18]);
        System.out.println(monopolyBoard[8] + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + monopolyBoard[19]);
        System.out.println(monopolyBoard[7] + monopolyBoard[6] + monopolyBoard[5] + monopolyBoard[4] + monopolyBoard[3] + monopolyBoard[2] + monopolyBoard[1] + monopolyBoard[0]);

    }

    /**
     * Method that gets places all the boxes on the board
     *
     * @param monopolyBoard            The 1D array that contains all of the boxes and array to be displayed
     * @param placedBoxesCounter       A counter created to count the number of placed boxes
     * @param randomNumGenerator       Random number generator
     * @param indexToBePlaced          The index of the box that will be placed in the array
     * @param ARRAY_SIZE               The size of the 1D array
     * @param NUMBER_OF_I_P_C_ST_BOXES The number of boxes for Invest, Party Hard, Chance, Steal boxes
     */
    public static void allBoxesPlacerOnBoard(String[] monopolyBoard, byte placedBoxesCounter, Random randomNumGenerator, int indexToBePlaced,
                                             final byte ARRAY_SIZE, final byte NUMBER_OF_I_P_C_ST_BOXES) {

        // String Definition for box types - Chance, Steal, Party, Invest boxes
        String chanceBox = "|C|";
        String stealBox = "|S|";
        String partyHardBox = "|P|";
        String investBox = "|I|";

        boxPlacer(monopolyBoard, placedBoxesCounter, randomNumGenerator, indexToBePlaced, ARRAY_SIZE, NUMBER_OF_I_P_C_ST_BOXES, chanceBox);

        boxPlacer(monopolyBoard, placedBoxesCounter, randomNumGenerator, indexToBePlaced, ARRAY_SIZE, NUMBER_OF_I_P_C_ST_BOXES, stealBox);

        boxPlacer(monopolyBoard, placedBoxesCounter, randomNumGenerator, indexToBePlaced, ARRAY_SIZE, NUMBER_OF_I_P_C_ST_BOXES, partyHardBox);

        boxPlacer(monopolyBoard, placedBoxesCounter, randomNumGenerator, indexToBePlaced, ARRAY_SIZE, NUMBER_OF_I_P_C_ST_BOXES, investBox);

        trapBoxPlacer(monopolyBoard, placedBoxesCounter, ARRAY_SIZE);

    }

    /**
     * Method that places last Box type[Trap Box]
     * It places the Trap boxes in last 7 empty places from the 1D array
     *
     * @param monopolyBoard The 1D Array
     * @param counter       A counter created to count the number of placed boxes
     * @param ARRAY_SIZE    The size of the 1D array
     */
    public static void trapBoxPlacer(String[] monopolyBoard, byte counter, final byte ARRAY_SIZE) {

        final byte NUM_OF_TRAP_BOXES = 7;
        byte placedTrapBoxes = 0;

        while (counter < ARRAY_SIZE) {

            if (monopolyBoard[counter].equalsIgnoreCase("|X|")) {

                monopolyBoard[counter] = "|T|";

                placedTrapBoxes++;
            }

            if (placedTrapBoxes == 7) {

                break;
            }
            ++counter;
        }
    }

    /**
     * Method that places all of the 4 type of boxes in the array
     *
     * @param monopolyBoard         The 1D Array
     * @param counter               A counter created to count the number of placed boxes
     * @param randomNumGenerator    Random number generator
     * @param indexToBePlaced       The index of the box that will be placed in the array
     * @param ARRAY_SIZE            The size of the 1D array
     * @param NUMBER_OF_I_C_P_S_BOX The number of each box type
     */
    public static void boxPlacer(String[] monopolyBoard, byte counter, Random randomNumGenerator, int indexToBePlaced, final byte ARRAY_SIZE, final byte NUMBER_OF_I_C_P_S_BOX, String boxType) {

        while (counter <= NUMBER_OF_I_C_P_S_BOX) {

            indexToBePlaced = randomNumGenerator.nextInt(ARRAY_SIZE - 1);

            if (monopolyBoard[indexToBePlaced].equalsIgnoreCase("|X|")) {

                monopolyBoard[indexToBePlaced] = boxType;

                ++counter;
            }
        }
    }


    /**
     * Method that reads a text file
     *
     * @param investBox       The file
     * @param investBoxReader The file reader
     * @param bufferedReader  The buffered reader
     * @return 2D array with all of the infermation about companies
     * @throws IOException
     */
    public static String[][] investBoxReader(File investBox, FileReader investBoxReader, BufferedReader bufferedReader) throws IOException {

        String[][] companies = new String[6][5];

        String lineReferences;

        String informationType = "";
        String theInformation = "";


        for (int coNumber = 0; coNumber < 6; coNumber++) {

            for (int counter = 0; counter < 5; counter++) {

                lineReferences = bufferedReader.readLine();

                String[] information = lineReferences.split("=");

                informationType = information[0];

                theInformation = information[1];

                if (information[0].equalsIgnoreCase("Company")) {

                    companies[coNumber][counter] = theInformation;
                } else if (information[0].equalsIgnoreCase("min")) {

                    companies[coNumber][counter] = theInformation;
                } else if (information[0].equalsIgnoreCase("coefficient")) {

                    companies[coNumber][counter] = theInformation;
                } else if (information[0].equalsIgnoreCase("lost")) {

                    companies[coNumber][counter] = theInformation;
                } else if (information[0].equalsIgnoreCase("win")) {

                    companies[coNumber][counter] = theInformation;
                }
            }
        }
        return companies;
    }
}