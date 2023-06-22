import java.io.*;
import java.util.*;

public class Othello {
    int turn;
    int winner;
    int board[][];
    int[][] steps = new int[][]{{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1},{0,1}};
    //add required class variables here

    public Othello(String filename) throws Exception {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        turn = sc.nextInt();
        //System.out.println(turn);
        board = new int[8][8];
        for(int i = 0; i < 8; ++i) {
            for(int j = 0; j < 8; ++j){
                board[i][j] = sc.nextInt();
                //System.out.print(board[i][j] + " ");
            }
            //System.out.println();
        }
        winner = -1;
        //Student can choose to add preprocessing here
    }

    //add required helper functions here

    private int eval(int[][] board , int turn){
        int wt = 0;
        int bt = 0;
        for(int i = 0 ; i<8 ; i++){
            for(int j = 0 ; j<8 ; j++){
                if(board[i][j] == 0){
                    bt+=1;
                }
                else if(board[i][j] == 1){
                    wt+=1;
                }
            }
        }
        if(turn == 0){
            return bt-wt;
        }
        else{
            return wt-bt;
        }
    }
    public int boardScore() {
        /* Complete this function to return num_black_tiles - num_white_tiles if turn = 0, 
         * and num_white_tiles-num_black_tiles otherwise. 
        */
       return eval(this.board , this.turn);
    }
    private ArrayList<int[]> get_all_locations(int[][] board , int color){
        ArrayList<int[]> ans = new ArrayList<int[]>();
        for(int i = 0 ; i<8 ; i++){
            for(int j = 0 ; j<8 ; j++){
                if (board[i][j]==color){
                    int[] coord = new int[]{i, j};
                    ans.add(coord);
                }
            }
        }
        return ans;
    }
    private ArrayList<Integer> get_all_possible_moves(int[][] board , int color){
        Set<Integer> valid_positions = new HashSet<Integer>();
        for(int[] box : get_all_locations(board , color)){
            ArrayList<Integer> temp = get_all_possible_moves_helper(board , box);
            // if(temp.size()!= 0){
            //     System.out.println("hi" + board[box[0]][box[1]]);
            // }
            valid_positions.addAll(temp);
        }
        
        // Set<Integer> ret = new HashSet<Integer>();
        // // for(int i = 0 ; i<valid_positions.size(); i++){
        // //     ret.add(8*valid_positions.get(i)[0] + valid_positions.get(i)[1]);
        // // }
        ArrayList<Integer> ans = new ArrayList<>();
        for(int a : valid_positions){
            ans.add(a);
        }
        return ans;
    }
    private ArrayList<Integer> get_all_possible_moves_helper(int[][] board , int[] box){
        int color = board[box[0]][box[1]];
        if(color==-1){return new ArrayList<Integer>();}
        ArrayList<Integer> moves_possible = new ArrayList<Integer>();
        for(int[] step : this.steps){
            //System.out.println(box[0]);
            //System.out.println(box[1]);
            int c = check_move(board , box , step);
            if(c != -1){
                moves_possible.add(c);
            }
        }
        return moves_possible;
    }
    private int check_move(int[][] board , int[] box , int[] step){
        int color = board[box[0]][box[1]];
        ArrayList<int[]> flips = new ArrayList<int[]>();
        // ArrayList<int[]> alpha = increment(board , box , step);
        // for(int i = 0 ; i<alpha.size();i++){
        //     //System.out.println(alpha.get(i)[0] +" "+ alpha.get(i)[1]);
        // }
        for(int[] b : increment(board , box , step)){
            if(board[b[0]][b[1]] == -1 && flips.size()!= 0){
                return 8*b[0] + b[1];
            }
            else if((board[b[0]][b[1]]== color) || (board[b[0]][b[1]]== -1 && flips.size() == 0 )){
                return -1;
            }
            else if(board[b[0]][b[1]]== (color+1)%2){
                flips.add(new int[]{b[0] , b[1]});
            }
        }
        return -1;
    }
    private static ArrayList<int[]> increment(int[][] board , int[] move, int[] step) {
        ArrayList<int[]> result = new ArrayList<>();
        int[] newMove = new int[2];
        newMove[0] = move[0] + step[0];
        newMove[1] = move[1] + step[1]; 
        while (newMove[0]>=0 && newMove[0]<8 && newMove[1]>=0 && newMove[1]<8) {
            //System.out.println(newMove[0]);
            //System.out.println(newMove[1]);
            result.add(new int[]{newMove[0] , newMove[1]});
            newMove[0] = newMove[0] + step[0];
            newMove[1] = newMove[1] + step[1];
        }
        // for(int i = 0 ;i<result.size() ; i++){
        //     System.out.println(result.get(i)[0] +" "+ result.get(i)[1]);
        // }
        return result;
    }
    private int[][] do_move(int[][] board , int[] move , int color){
        ArrayList<int[]> fliping_square = new ArrayList<>();
        for(int[] step: steps){
            fliping_square.addAll(get_flipping_square(board , move , step , color));
        }
        //System.out.println(move[0] + " " + move[1]);
        // for(int[] a: fliping_square){
        //     System.out.println(a[0] + " " + a[1]);
        // }
        for(int[] box : fliping_square){
            board[box[0]][box[1]] = color;
        }
        return board;
    }
    private ArrayList<int[]> get_flipping_square(int[][] board , int[] move , int[] step , int color){
        ArrayList<int[]> fliping_square = new ArrayList<>();
        fliping_square.add(move);
        ArrayList<int[]> temp = new ArrayList<>();
        for(int[] box : increment(board , move , step)){
            if(board[box[0]][box[1]]== -1){
                break;
            }
            else if(board[box[0]][box[1]]== color && (fliping_square.size()>1 || box != move)){
                //System.out.println();
                return fliping_square;
            }
            else if(board[box[0]][box[1]]== (color+1)%2){
                //System.out.println(box[0] +" "+ box[1]);
                fliping_square.add(box);
            }
        }
        return temp;
    }
    public int bestMove(int k) {
        /* Complete this function to build a Minimax tree of depth k (current board being at depth 0),
         * for the current player (siginified by the variable turn), and propagate scores upward to find
         * the best move. If the best move (move with  score at depth 0) is i,j; return i*8+j
         * In case of ties, return the smallest integer value reprmaxesenting the tile with best score.
         * 
         * Note: Do not alter the turn variable in this function, so that the boardScore() is the score
         * for the same player throughout the Minimax tree.
        */
        //System.out.println(k + " k");
        int[] ans = minimax(this.board , 0 ,  k);
        //System.out.println();
        //System.out.println(ans[0]);
        //System.out.println(8*ans[1] +ans[2] );
        
        return 8*ans[1] + ans[2];
    }

    public ArrayList<Integer> fullGame(int k) {
        /* Complete this function to compute and execute the best move for each player starting from
         * the current turn using k-step look-ahead. Accordingly modify the board and the turn
         * at each step. In the end, modify the winner variable as required.
         */
        //System.out.println(k+" k");
        ArrayList<Integer> moves_done = new ArrayList<Integer>();
        int i = bestMove(k);
        boolean b;
        if(i == -9){b = false;}
        else{b = true;}
        while(i!= -9 || b == false){
            //System.out.println(moves_done);
            //System.out.println(i);
            if(i!= -9){
                moves_done.add(i);
                board = do_move(board , new int[]{i/8 , i%8} , turn);
                b = true;
                // System.out.println(i + " " + turn);
                // for(int alpha = 0 ; alpha<8 ; alpha++){
                //     for(int beta = 0 ; beta<8 ; beta++){
                //         System.out.print(board[alpha][beta] + " ");
                //     }
                //     System.out.println();
                // }
            }
            turn = (turn+1)%2;
            i = bestMove(k);
            if(i == -9){
                b = !b;
            }
        }
        int e = eval(board , 0);
        //System.out.println(e);
        if(e>0){
            winner = 0;
        }
        else if(e<0){
            winner = 1;
        }
        else{
            winner = -1;
        }
        return moves_done;
    }
    private int[] minimax(int[][] board , int depth , int k ){

        if(depth == k){
            return new int[]{eval(board , 0) , -1 , -1};
        }
        else{
            ArrayList<Integer> move = get_all_possible_moves(board , (turn+depth)%2);
            // for(int[] ab: move){
            //     System.out.println(8*ab[0] +ab[1]);
            // }
            if(move.size() == 0){
                return new int[]{minimax(board , depth+1 , k)[0] , -1 , -1};
            }
            else{
                int bestsc; 
                int[] next_move = new int[2];
                next_move[0] = 8;
                next_move[1] = 8;
                if((turn+depth)%2 == 0){bestsc = -2147483648;}
                else{bestsc = 2147483647;}
                //System.out.println(move.size());
                for(int i = 0 ; i< move.size() ; i++){
                    // System.out.println(move.get(i)[0] + " " + move.get(i)[1]);
                    //System.out.println(8*move.get(i)[0] + move.get(i)[1] + " " + depth);
                    int[][] board_copy = get_copy(board);
                    board_copy = do_move(board_copy , new int[]{move.get(i)/8 , move.get(i)%8}, (turn+depth)%2);
                    // System.out.println(8*move.get(i)[0] + move.get(i)[1]);
                    // System.out.println(eval(board_copy,0));
                    // for(int alpha = 0 ; alpha<8 ; alpha++){
                    //     for(int beta = 0 ; beta<8 ; beta++){
                    //         System.out.print(board_copy[alpha][beta]+" ");
                    //     }
                    //     System.out.println();
                    // }
                    // System.out.println();
                    // for(int alpha = 0 ; alpha<8 ; alpha++){
                    //     for(int beta = 0 ; beta<8 ; beta++){
                    //         System.out.print(board[alpha][beta]+" ");
                    //     }
                    //     System.out.println();
                    // }
                    int[] temp = minimax(board_copy , depth+1 , k);
                    //System.out.println(temp[0]);
                    if((turn+depth)%2 == 0){
                        if(temp[0]>bestsc){
                            bestsc = temp[0];
                            next_move[0] = move.get(i)/8;
                            next_move[1] = move.get(i)%8;
                        }
                        else if(temp[0]== bestsc){
                            if(move.get(i)<= 8*next_move[0]+next_move[1]){
                                bestsc = temp[0];
                                next_move[0] = move.get(i)/8;
                                next_move[1] = move.get(i)%8;
                            }
                        }
                    }
                    else{
                        if(temp[0]<bestsc){
                            bestsc = temp[0];
                            next_move[0] = move.get(i)/8;
                            next_move[1] = move.get(i)%8;
                        }
                        else if(temp[0]== bestsc){
                            if(move.get(i)<= 8*next_move[0]+next_move[1]){
                                bestsc = temp[0];
                                next_move[0] = move.get(i)/8;
                                next_move[1] = move.get(i)%8;
                            }
                        }
                    }
                }
                //System.out.println();
                return new int[]{bestsc , next_move[0] , next_move[1]};
            }
        }
    }
    // public void print(){
    //     for(int i = 0 ; i<8 ; i++){
    //         for(int j = 0 ; j<8 ; j++){
    //             System.out.print(board[i][j] + " ");
    //         }
    //         System.out.println();
    //     }
    // }
    public int[][] getBoardCopy() {
        int copy[][] = new int[8][8];
        for(int i = 0; i < 8; ++i)
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        return copy;
    }
    private int[][] get_copy(int[][] board){
        int copy[][] = new int[8][8];
        for(int i = 0; i < 8; ++i)
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        return copy;
    }

    public int getWinner() {
        //System.out.print("winner");
        return winner;
    }

    public int getTurn() {
        //System.out.print("turn");
        return turn;
    }
}