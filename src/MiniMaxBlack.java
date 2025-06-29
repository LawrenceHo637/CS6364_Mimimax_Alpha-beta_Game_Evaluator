import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

public class MiniMaxBlack {
    public static void main(String[] args) {
        if(args.length != 3){
            System.err.println("Usage: java MiniMax <input_file> <output_file> <depth>");
            return;
        }

        // Read position info from the input file and save each board position in an array
        File input = new File(args[0]);
        String[] position = new String[16];

        try{
            // If input file exists, read the file
            Scanner inputReader = new Scanner(input);
            String temp = inputReader.nextLine();

            for(int i = 0; i < position.length; i++){
                position[i] = "" + temp.charAt(i);
            }
            inputReader.close();
        } catch(FileNotFoundException e) {
            // If input file doesn't exist, return an error message and exit
            System.err.println("Error: File does not exist");
            return;
        }

        // Lists for storing the possible moves and their corresponding static estimation
        ArrayList<BlackMove> moves = new ArrayList<BlackMove>();
        GameCalculations game = new GameCalculations();

        // Loop through array of positions and generate all possible moves for White for a given depth
        BlackMove evaluation = evaluate_moves_first(game, position, moves, Integer.parseInt(args[2]));

        // Output final game statistics
        String outStr = "";
        for(int i = 0; i < evaluation.getPosition().length; i++){
            outStr += evaluation.getPosition()[i];
        }
        System.out.print("Board Position: " + outStr);
        System.out.println("\nPositions evaluated by static estimation: " + moves.size());
        System.out.println("MINIMAX-Black estimate: " + evaluation.getEstimate());

        // Write to output file
        try{
            FileWriter outputWriter = new FileWriter(args[1]);
            outputWriter.write(outStr);
            outputWriter.close();
        } catch (IOException e) {
            System.err.println("Error: Failed to write to file");
        }
    }

    // Method for reversing a position to make a move for Black
    public static String[] flip_position(String[] pos){
        String[] reverse = Arrays.copyOf(pos, pos.length);
        String temp;
        int len = reverse.length;

        // Reverse the position string
        for(int i = 0; i < len/2; i++){
            temp = reverse[i];
            reverse[i] = reverse[len - 1 - i];
            reverse[len - 1 - i] = temp;
        }

        // Replace all Black piece with White pieces and all White pieces with Black pieces
        for(int i = 0; i < len; i++){
            if(reverse[i].equals("W")){
                reverse[i] = "B";
            } else if(reverse[i].equals("w")){
                reverse[i] = "b";
            } else if(reverse[i].equals("B")){
                reverse[i] = "W";
            } else if(reverse[i].equals("b")){
                reverse[i] = "w";
            }
        }

        return reverse;
    }

    // Recursive method for generating all moves at a depth specified in the parameters
    public static BlackMove evaluate_moves_first(GameCalculations game, String[] pos, ArrayList<BlackMove> moves, int depth) {
        ArrayList<BlackMove> possibleMoves = new ArrayList<BlackMove>();
        int bestVal;
        int min_max = 0;
        int index = 0;
        BlackMove temp = new BlackMove();

        if(depth == 0) {
            // Return the position and its estimate if the depth is 0
            temp.setMove(pos, game.estimate_position(pos));
            moves.add(temp);
            return temp;
        }

        // Check if White or Black has the won the game and return the position and estimate if true
        int estimate = game.estimate_position(pos);
        if(estimate == 100 || estimate == -100){
            temp.setMove(pos, estimate);
            moves.add(temp);
            return temp;
        }

        bestVal = 1000;

        for (int i = 0; i < pos.length; i++) {
            if (pos[i].equals("B") || pos[i].equals("b")) {
                temp = evaluate_moves(game, flip_position(game.generate_move(flip_position(pos), (i - 15) * -1)), moves, depth - 1, true);
                possibleMoves.add(temp);

                if (bestVal > temp.getEstimate()) {
                    bestVal = temp.getEstimate();
                    min_max = index;
                }
                index++;

            }
        }

        return possibleMoves.get(min_max);
    }

    // Recursive helper method of evaluate_moves_first
    public static BlackMove evaluate_moves(GameCalculations game, String[] pos, ArrayList<BlackMove> moves, int depth, boolean max) {
        ArrayList<BlackMove> possibleMoves = new ArrayList<BlackMove>();
        int bestVal;
        int min_max = 0;
        int index = 0;
        BlackMove temp = new BlackMove();

        if(depth == 0) {
            // Return the position and its estimate if the depth is 0
            temp.setMove(pos, game.estimate_position(pos));
            moves.add(temp);
            return temp;
        }

        // Check if White or Black has the won the game and return the position and estimate if true
        int estimate = game.estimate_position(pos);
        if(estimate == 100 || estimate == -100){
            temp.setMove(pos, estimate);
            moves.add(temp);
            return temp;
        }

        if (max) {
            // Code for the maximizer (White's turn)
            bestVal = Integer.MIN_VALUE;

            // For each White piece on the board, evaluate the next move
            for (int i = 0; i < pos.length; i++) {
                if (pos[i].equals("W") || pos[i].equals("w")) {
                    temp = evaluate_moves(game, game.generate_move(pos, i), moves, depth - 1, false);
                    possibleMoves.add(temp);

                    if (bestVal < temp.getEstimate()) {
                        bestVal = temp.getEstimate();
                        min_max = index;
                    }
                    index++;
                }
            }

            // Amongst all moves generated, return the position with the highest estimate
            temp.setMove(pos, possibleMoves.get(min_max).getEstimate());
            return temp;
        } else {
            // Code for the minimizer (Black's turn)
            bestVal = Integer.MAX_VALUE;

            // For each Black piece on the board, evaluate the next move
            for (int i = 0; i < pos.length; i++) {
                if (pos[i].equals("B") || pos[i].equals("b")) {
                    temp = evaluate_moves(game, flip_position(game.generate_move(flip_position(pos), (i - 15) * -1)), moves, depth - 1, true);
                    possibleMoves.add(temp);

                    if (bestVal > temp.getEstimate()) {
                        bestVal = temp.getEstimate();
                        min_max = index;
                    }
                    index++;

                }
            }

            // Amongst all moves generated, return the position with the highest estimate
            temp.setMove(pos, possibleMoves.get(min_max).getEstimate());
            return temp;
        }
    }

    static class BlackMove{
        String[] position;
        int estimate;

        public void setMove(String[] position, int estimate){
            this.position = position;
            this.estimate = estimate;
        }

        public String[] getPosition(){
            return position;
        }

        public int getEstimate(){
            return estimate;
        }
    }

    // Subclass containing methods for the Move Generator, Static Estimator, and determining if White or Black has won
    static class GameCalculations {
        // Method for generating the move of a single White piece on the game board
        public String[] generate_move(String[] board, int i){
            String[] P = Arrays.copyOf(board, board.length);

            if(i == 15){
                // if White is at the right end of the board, jump out
                P[i] = "x";
            } else {
                // if there are no free spaces from i to the last position, jump out of the board
                for(int j = i + 1; j < P.length; j++){
                    if(j == 15 && !P[j].equals("x")) {
                        // if no free space on the board, jump out of the game board
                        P[i] = "x";
                        break;
                    } else if(P[j].equals("x")) {
                        // if a free space is found, advance/jump to it
                        P[j] = P[i];
                        P[i] = "x";

                        // if White jumps over a single Black piece, move the Black piece to the rightmost free position
                        if((j - i == 2) && (P[j - 1].equals("b") || P[j - 1].equals("B"))){
                            for(int k = P.length - 1; k >= 0; k--){
                                if(P[k].equals("x")){
                                    if(k != j - 2){
                                        P[k] = P[j - 1];
                                        P[j - 1] = "x";
                                    }
                                    break;
                                }
                            }
                        }

                        break;
                    }
                }
            }

            return P;
        }

        // Method for calculating the static estimate of a position
        // First checks if White or Black has won and returns 100 or -100 respectively if true
        // Otherwise, calculates (i + j - 15), where i is the index of W and j is the index of B, and return it
        public int estimate_position(String[] P){
            int i = white_win(P);
            int j = black_win(P);

            if(i == -1){
                return 100;
            } else if(j == -1){
                return -100;
            } else {
                return (i + j - 15);
            }
        }

        // Method for checking if White has won the game by checking if "W" is present on the board
        public int white_win(String[] P){
            for(int i = 0; i < P.length; i++){
                if(P[i].equals("W")){
                    return i;
                }
            }

            return -1;
        }

        // Method for checking if Black has won the game by checking if "B" is present on the board
        public int black_win(String[] P){
            for(int i = 0; i < P.length; i++){
                if(P[i].equals("B")){
                    return i;
                }
            }

            return -1;
        }
    }
}
