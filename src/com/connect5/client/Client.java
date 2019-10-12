package com.connect5.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private final int PORT = 5000;
    private Socket requestSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Scanner inputScanner;
    private String message = "";
    private String response;
    private int choice;
    private boolean gameInProgress = false;

    public void run() {

        inputScanner = new Scanner(System.in);

        try {
            System.out.println("5 In Line Game application!");

            requestSocket = new Socket("localhost", PORT);
            System.out.println("Connected to localhost on port " + PORT);

            objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(requestSocket.getInputStream());


            do {
                try {
                    //Initial Server greeting
                    response = (String)objectInputStream.readObject();
                    System.out.println(response);

                    do {
                        System.out.println("Type 1 to connect to the game;");
                        System.out.println("Type 2 to exit the application;");
                        choice = inputScanner.nextInt();
                        inputScanner.nextLine();
                    } while (choice < 0 || choice > 2);

                    System.out.println("Selected " + choice);

                    switch (choice) {
                        case 1:
                            if (!this.gameInProgress) {
                                playTheGame();
                            }else{
                                System.out.println("Game already in progress!");
                            }
                            break;
                        case 2:
                            exitClientApplication();
                            break;
                    }

                }catch (ClassNotFoundException ce){
                    System.out.printf("Error: %s \n", (ce));
                }
            }while (!message.equalsIgnoreCase("bye"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                objectInputStream.close();
                objectOutputStream.close();
                requestSocket.close();
            } catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

    }

    private void playTheGame() {
        message = "play";
        sendMessage(message);
        int gameChoice = 0;
        boolean isPlaying = false;

        try {
            //Select name
            response = (String)objectInputStream.readObject();
            System.out.println(response);
            message = inputScanner.nextLine();
            sendMessage(message);

            //Hello "Name"
            response = (String)objectInputStream.readObject();
            System.out.println(response);

            //Returns Symbol for the game
            response = (String)objectInputStream.readObject();
            System.out.println(response);

            //Returns Opponent name for the game
            response = (String)objectInputStream.readObject();
            System.out.println(response);

            //game on.
            do {
                //Select what to do
                do {
                    System.out.println("Type 1 to check game board state;");
                    System.out.println("Type 2 to make a move;");
                    System.out.println("Type 3 to exit the game;");
                    gameChoice = inputScanner.nextInt();
                    inputScanner.nextLine();
                } while (gameChoice < 0 || gameChoice > 3);

                switch (gameChoice) {
                    case 1:
                        printGameBoardState();
                        isPlaying = true;
                        break;
                    case 2:
                        makeAMove();
                        isPlaying = true;
                        break;
                    case 3:
                        exitGame();
                        isPlaying = false;
                        break;
                }
            } while (isPlaying);



        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void exitGame(){
        message = "exitGame";
        sendMessage(message);

    }

    private void makeAMove() {
        boolean moveSuccess = false;
        boolean okToMove = false;

        message = "makeAMove";
        sendMessage(message);

        //OK to move?
        try {
            response = (String)objectInputStream.readObject();
            if (response.equalsIgnoreCase("okToMove")) {
                System.out.println("Its Your Turn");
                okToMove = true;
            }else if(response.equalsIgnoreCase("notYourTurn")){
                System.out.printf("NOT YOUR TURN!%n");
                okToMove = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (okToMove) {
            do {

                int moveChoice = 0;
                do {
                    System.out.println("Select column on the board<1-9> to drop your Token into");
                    moveChoice = inputScanner.nextInt();
                    inputScanner.nextLine();
                } while (moveChoice < 0 || moveChoice > 9);


                message = String.valueOf(moveChoice);
                sendMessage(message);
                try {
                    //OK
                    //Not your turn --> can be done above
                    //Redo
                    //Win
                    //Tie
                    //Returned move confirmation
                    response = (String)objectInputStream.readObject();
                    System.out.println(response);

                    switch (response) {
                        case "moveOk":
                            //acknowledge move
                            System.out.printf("Move performed successfully on column %d%n", moveChoice);
                            moveSuccess = true;
                            okToMove = false;
                            break;
                        case "redoMove":
                            System.out.printf("Redo your move %n");
                            //Ask to redo move as slot is taken
                            moveSuccess = false;
                            okToMove= true;
                            break;
                        case "youWon":
                            //congrats message + end game
                            break;
                        case "draw":
                            //Game ended in a Draw + end game
                            break;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } while (!moveSuccess);
        }
    }

    private void printGameBoardState(){
        message = "showGameBoardState";
        sendMessage(message);

        try {
            //Returned Game Board State printout
            response = (String)objectInputStream.readObject();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void exitClientApplication() {
        message = "bye";
        sendMessage(message);
    }

    void sendMessage(String msg)
    {
        try{
            objectOutputStream.writeObject(msg);
            objectOutputStream.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }


}
