package de.arvato.stratego;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.arvato.stratego.colyseum.ColyseusManager;
import de.arvato.stratego.colyseum.Player;
import de.arvato.stratego.game.HistoryPiece;
import de.arvato.stratego.game.Piece;
import de.arvato.stratego.game.PieceEnum;
import de.arvato.stratego.game.PieceFightStatus;
import de.arvato.stratego.model.MoveView;
import de.arvato.stratego.model.PiecesView;
import de.arvato.stratego.model.PlayerView;
import de.arvato.stratego.model.TurnView;
import de.arvato.stratego.util.SpacingItemDecoration;

public class StrategoView {

    public static final String TAG = "StrategoView";

    private StrategoViewBase strategoViewBase;
    private StrategoActivity parent;
    //private LayoutInflater inflater;
    private StrategoControl strategoControl;
    private ViewAnimator viewAnimator;
    private int selectedPos;
    private List<Integer> nextMovements;
    private int player;
    private boolean multiplayer;
    private Timer timer;
    private TextView textViewClockTimeTop, textViewClockTimeBottom;
    //private ImageView pieceFight1, pieceFight2;
    //private View fightView;
    private Drawable drawableFightWin, drawableFightLost;
    private Pair latestFight;
    private TextView winnerTextView;

    private RecyclerView recyclerViewRed;
    private RecyclerView recyclerViewBlue;

    private StrategoCapturedPieceAdapter adapterBlue;
    private StrategoCapturedPieceAdapter adapterRed;

    private List<CapturedPieceItem> listCapturedPiecesBlue;
    private List<CapturedPieceItem> listCapturedPiecesRed;

    private ImageView fightPiece1;
    private ImageView fightPiece2;

    private ImageView fightPiece1Indicator;
    private ImageView fightPiece2Indicator;
    private int totalFighths=0;
    private ColyseusManager colyseusManager;

    //observers view model
    private MutableLiveData<PlayerView> playerLiveData = new MutableLiveData<>();
    private MutableLiveData<PiecesView> piecesLiveData = new MutableLiveData<>();
    private MutableLiveData<TurnView> gameStartLive = new MutableLiveData<>();
    private MutableLiveData<MoveView> moveLiveData = new MutableLiveData<>();
    private MutableLiveData<String> finishGameLive = new MutableLiveData<>();

    private String prefferedUserName;

    private TextView playerUpTitle;

    static class StrategoInnerHandler extends Handler {
        WeakReference<StrategoView> _strategoView;

        StrategoInnerHandler(StrategoView view) {
            _strategoView = new WeakReference<StrategoView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            StrategoView strategoView = _strategoView.get();
            if (strategoView != null) {
                int currentPlayer = strategoView.getPlayer();
                long lTmp;
                lTmp = strategoView.getStrategoControl().getRedRemainClock();
                if (lTmp < 0) {
                    lTmp = -lTmp;
                }

                if (currentPlayer == StrategoConstants.RED) {
                    strategoView.textViewClockTimeBottom.setText(strategoView.formatTime(lTmp));
                } else {
                    strategoView.textViewClockTimeTop.setText(strategoView.formatTime(lTmp));
                }

                lTmp = strategoView.getStrategoControl().getBlueRemainClock();
                if (lTmp < 0) {
                    lTmp = -lTmp;
                }
                if (currentPlayer == StrategoConstants.RED) {
                    strategoView.textViewClockTimeTop.setText(strategoView.formatTime(lTmp));
                } else {
                    strategoView.textViewClockTimeBottom.setText(strategoView.formatTime(lTmp));
                }
            }
        }
    }

    protected StrategoInnerHandler m_timerHandler;


    public StrategoView(Activity activity) {
        super();
        selectedPos=-1;

        Intent intent = activity.getIntent();
        player = intent.getIntExtra("select_color", StrategoConstants.RED);
        multiplayer = intent.getBooleanExtra("multiplayer", false);
        Log.d(TAG, "Player Selected color: " + player);

        //select your player
        //player = StrategoConstants.RED;

        strategoControl = new StrategoControl(player, multiplayer);
        //strategoControl.addObserver(this);

        parent = (StrategoActivity) activity;
        strategoViewBase = new StrategoViewBase(activity, player);
        //inflater = (LayoutInflater) parent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        drawableFightWin = ContextCompat.getDrawable(parent.getApplicationContext(), R.drawable.fight_win);
        drawableFightLost =  ContextCompat.getDrawable(parent.getApplicationContext(), R.drawable.fight_lost);

        //Listeners
        View.OnClickListener ocl = new View.OnClickListener() {
            public void onClick(View arg0) {
                handleClick(strategoViewBase.getIndexOfButton(arg0));
            }
        };
        View.OnLongClickListener olcl = new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                handleClick(strategoViewBase.getIndexOfButton(view));
                return true;
            }
        };
        strategoViewBase.init(ocl, olcl);

        //View Animator
        viewAnimator = (ViewAnimator) parent.findViewById(R.id.ViewAnimatorMain);
        if (viewAnimator != null) {
            viewAnimator.setOutAnimation(parent, R.anim.slide_left);
            viewAnimator.setInAnimation(parent, R.anim.slide_right);
        }

        initPreferences ();

        initDistributePieces () ;

        // Init the captured View
        initCapturedImages ();

        //initDialogSelectColor ();

        // Timer
        initTimer ();

        initFightView();

        if (multiplayer) {
            initObservers ();
            initColyseusManager();
        }

        // Paint
        paintBoard();

    }

    private void initPreferences() {
        SharedPreferences sharedPreferences  = parent.getSharedPreferences(StrategoConstants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefferedUserName = sharedPreferences.getString("name", "Hidden_Name");
        Log.d(TAG, "PrefferedName:" + prefferedUserName);
    }

    private void initObservers() {
        playerLiveData.observeForever(new Observer<PlayerView>() {
            @Override
            public void onChanged(PlayerView playerView) {
                Log.d(TAG, "Player View changed:" + playerView);
                parent.runOnUiThread(() -> {
                    playerUpTitle.setText(playerView.getName());
                });
            }
        });

        piecesLiveData.observeForever(new Observer<PiecesView>() {
            @Override
            public void onChanged(PiecesView playerView) {
                Log.d(TAG, "Pieces View changed:" + playerView);
                parent.runOnUiThread(() -> {
                    updateEnemyBoard(playerView);
                });
            }
        });

        gameStartLive.observeForever(new Observer<TurnView>() {
            @Override
            public void onChanged(TurnView turnView) {
                Log.d(TAG,"Game Start Live:" + turnView.getTurn() + " ready to move");
                strategoControl.getBoard().initTurn(turnView.getTurn());
                strategoControl.setMultiPlayerReady(true);
                strategoControl.continueTimer();
            }
        });

        moveLiveData.observeForever(new Observer<MoveView>() {
            @Override
            public void onChanged(MoveView moveView) {
                Log.d(TAG,"Move Live Data:" + moveView);
                MoveView transformed = moveView.transformMoveToEnemyBoard();
                boolean b = strategoControl.movePieceEnemy (transformed.getFrom(), transformed.getTo());

                if (!b) {
                    // Error moving from enemy TODO WHAT?
                    Toast.makeText(parent.getApplicationContext(), "Error moving enemy piece!", Toast.LENGTH_LONG).show();
                }
                paintBoard();
                updateStatus();
            }
        });

        finishGameLive.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                strategoControl.getBoard().finishGame();
                updateFinishedGameStateEvent();
            }
        });
    }

    private void initColyseusManager() {
        colyseusManager = ColyseusManager.getInstance(StrategoConstants.ENDPOINT_COLYSEUS);
        colyseusManager.setPlayerLiveData(playerLiveData);
        colyseusManager.setPiecesLiveData(piecesLiveData);
        colyseusManager.setGameStartLive(gameStartLive);
        colyseusManager.setMoveLiveData(moveLiveData);
        colyseusManager.setFinishGameLive(finishGameLive);

        Player enemyPlayer = colyseusManager.getEnemyPlayer();
        if (enemyPlayer != null) {
            playerUpTitle.setText(enemyPlayer.name);
        }

        PiecesView enemyPieces = colyseusManager.getEnemyPieces();
        if (enemyPieces != null) {
            updateEnemyBoard (enemyPieces);
        }

    }

    private synchronized void updateEnemyBoard(PiecesView enemyPieces) {
        Piece[] pieces = enemyPieces.transformToPieceModel(player == StrategoConstants.RED ? StrategoConstants.BLUE : StrategoConstants.RED);
        int ini, end;
        ini = StrategoConstants.UP_PLAYER[0];
        end = StrategoConstants.UP_PLAYER[1];
        for (int z=ini; z<end;z++) {
            Piece piece = pieces[ (end-1) -z];
            strategoControl.getBoard().setPieceAt(piece, z);
        }
        paintBoard();
        Toast.makeText(parent.getApplicationContext(), "Pieces Enemy Ready!", Toast.LENGTH_LONG).show();
    }

    public void handleClick(int index) {
        Log.i(TAG, "handleClick at index:" + index);
        nextMovements = Collections.emptyList();
        if (strategoControl.selectPiece(index)) {
            Log.d (TAG, "Piece at position:" + index + " has been selected.");
            nextMovements = strategoControl.getBoard().getPossibleMovements (index);
            selectedPos = index;
            StringBuilder sb = new StringBuilder();
            for (Integer i : nextMovements) {
                sb.append(i);
                sb.append(",");
            }
            if (sb.length() > 0) sb.deleteCharAt(sb.length()-1);
            Log.d(TAG, "Possible movements at index:" + index + " = " + sb.toString());
        } else {
            if (selectedPos != -1) {
                Log.d (TAG, "Move Piece to target index:" + index);
                boolean moved = strategoControl.movePiece(index);
                if (moved) {
                    colyseusManager.sendMove (selectedPos, index);
                    checkFinishedGame();
                    selectedPos = -1;
                }
            }
        }
        strategoViewBase.paintBoard(strategoControl, selectedPos, nextMovements);
        updateStatus();
    }

    private void checkFinishedGame() {
        if (strategoControl.getBoard().getGameStatus() == StrategoConstants.GameStatus.FINISH) {
            colyseusManager.sendFinishGame ();
            updateFinishedGameStateEvent();
        }
    }

    private void updateFinishedGameStateEvent () {
        viewAnimator.setDisplayedChild(0);
        int winner = strategoControl.getWinner ();
        if (StrategoConstants.BLUE == winner) {
            winnerTextView.setText("Blue Flag Captured!");
            winnerTextView.setTextColor(Color.BLUE);
        } else {
            winnerTextView.setText("Red Flag Captured!");
            winnerTextView.setTextColor(Color.RED);
        }
        strategoControl.stopTimer();
        winnerTextView.setVisibility(View.VISIBLE);
        Animation bounceAnimation = AnimationUtils.loadAnimation(parent.getApplicationContext(), R.anim.bounce);
        winnerTextView.startAnimation(bounceAnimation);
    }

    private void initDistributePieces() {
        //TODO Add a grid with initial pieces? or just initialize it random
        /*GridView gridView = parent.findViewById(R.id.startGameGrid);
        PieceEnum startPieces [] = StrategoControl.createStartPiece(this.player);
        StrategoPieceAdapter strategoPieceAdapter = new StrategoPieceAdapter(parent.getApplicationContext(), startPieces, player);
        gridView.setAdapter(strategoPieceAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Item clicked: " + position + " id:" + id);
                    startPieces[position] = null;
                    strategoPieceAdapter.notifyDataSetChanged();
                }
            }
        );*/

        Button bPlayerReady = parent.findViewById(R.id.PlayerReady);
        //Button bPlayerReset = parent.findViewById(R.id.PlayerReset);
        Button bPlayerRandom = parent.findViewById(R.id.PlayerRandom);
        Button bShowPiece = parent.findViewById(R.id.ShowPiece);
        Button bLeaveRoom = parent.findViewById(R.id.LeaveRoom);
        //Button bFakeRoom = parent.findViewById(R.id.FakeMove);
        TextView InfoText = parent.findViewById(R.id.InfoText);
        TextView InfoText2 = parent.findViewById(R.id.InfoText2);
        TextView InfoText3 = parent.findViewById(R.id.InfoText3);
        winnerTextView = parent.findViewById(R.id.winnerTextView);

        Drawable drawableCircleRed = ContextCompat.getDrawable(parent.getApplicationContext(), R.drawable.turnred);
        Drawable drawableCircleBlue = ContextCompat.getDrawable(parent.getApplicationContext(), R.drawable.turnblue);

        ImageView turnPlayerUp = parent.findViewById(R.id.turnPlayerUp);
        ImageView turnPlayerDown = parent.findViewById(R.id.turnPlayerDown);
        playerUpTitle = parent.findViewById(R.id.playerUpTitle);
        TextView playerDownTitle = parent.findViewById(R.id.playerDownTitle);

        if (player == StrategoConstants.RED) {
            turnPlayerDown.setImageDrawable(drawableCircleRed);
            turnPlayerUp.setImageDrawable(drawableCircleBlue);
        } else {
            turnPlayerUp.setImageDrawable(drawableCircleRed);
            turnPlayerDown.setImageDrawable(drawableCircleBlue);
        }

        playerDownTitle.setText(prefferedUserName);
        playerUpTitle.setText("Waiting for player . . .");

        bPlayerReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = strategoControl.startGame();
                if (!b) {
                    Toast.makeText(parent.getApplicationContext(), "Fill all pieces!", Toast.LENGTH_SHORT).show();
                } else {
                    viewAnimator.setDisplayedChild(1);
                    InfoText.setVisibility(View.GONE);
                    InfoText2.setVisibility(View.GONE);
                    InfoText3.setVisibility(View.GONE);
                    bPlayerReady.setEnabled(false);
                    bPlayerRandom.setEnabled(false);
                    colyseusManager.setPieces (strategoControl.getBoard().getPiecesBottom());
                }
            }
        }
        );

        /*bPlayerReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                winnerTextView.setVisibility(View.GONE);

                // Init the captured View
                //initCapturedImages ();

                // Timer
                //initTimer ();

                strategoControl.resetGame();

                // Paint
                paintBoard();

            }
        });*/

        bPlayerRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strategoControl.randomPieces(player);
                //strategoControl.continueTimer();
                paintBoard ();
            }
        }
        );

        bShowPiece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //strategoControl.randomPieces(player);
                //paintBoard ();
                //Toast.makeText(parent.getApplicationContext(), "Show piece at position test!", Toast.LENGTH_SHORT).show();

                //Piece discovery test
                //Piece piece = strategoControl.getBoard().getPieceAt(34);
                //Log.d(TAG, piece.toString());
                //piece.setPieceDiscovered(true);
                //StrategoImageView imageView = strategoViewBase.get_arrImages()[34];
                //Log.d (TAG, imageView.toString());
                //imageView.invalidate();
                //strategoViewBase.paintBoard(strategoControl, -1, Collections.emptyList());

                //history test
                /*List<HistoryPiece> historyMoves = strategoControl.getBoard().getHistoryMoves();
                Log.d(TAG, "History size:" + historyMoves.size());
                for (HistoryPiece history : historyMoves) {
                    Log.d(TAG, history.toString());
                }*/

                /*String roomId = colyseusManager.getRoomID();
                Log.d(TAG, roomId);*/

                //colyseusManager.sendFakeMessage ();

                //strategoControl.getBoard().printPieces();

                //strategoControl.continueTimer();
                //strategoControl.getBoard().changeTurn();

                //strategoControl.getBoard().getTurn();
                /*Log.d(TAG, "Turn before change:" + strategoControl.getBoard().getTurn());
                strategoControl.switchTimer();
                strategoControl.getBoard().changeTurn();
                Log.d(TAG, "Turn after change:" + strategoControl.getBoard().getTurn());*/

            }
        }
        );

        /*bFakeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    colyseusManager.sendFakeMove();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });*/

        bLeaveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colyseusManager.disconnect();
            }
        });

    }

    private void initCapturedImages() {
        //Recycler view for Red
        recyclerViewRed = parent.findViewById(R.id.recyclerViewRed);
        GridLayoutManager gridLayoutManagerRed = new GridLayoutManager(parent, 2);
        recyclerViewRed.setLayoutManager(gridLayoutManagerRed);
        //set spacing between columns
        int spacingInPixels = parent.getResources().getDimensionPixelSize(R.dimen.grid_item_spacing);
        recyclerViewRed.addItemDecoration(new SpacingItemDecoration(spacingInPixels));

        /*LinearLayoutManager layout = new LinearLayoutManager(parent);
        layout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layout);*/

        listCapturedPiecesRed = new ArrayList<>();
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.MARSHALL, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.GENERAL, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.COLONEL, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.MAJOR, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.CAPTAIN, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.LIEUTENANT, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.SERGEANT, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.MINER, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.SCOUT, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.SPY, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.BOMB, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.FLAG, StrategoConstants.RED, 0));
        adapterRed = new StrategoCapturedPieceAdapter(listCapturedPiecesRed);
        recyclerViewRed.setAdapter(adapterRed);

        //Recycler view for Blue
        recyclerViewBlue = parent.findViewById(R.id.recyclerViewBlue);
        GridLayoutManager gridLayoutManagerBlue = new GridLayoutManager(parent, 2);
        recyclerViewBlue.setLayoutManager(gridLayoutManagerBlue);

        recyclerViewBlue.addItemDecoration(new SpacingItemDecoration(spacingInPixels));
        listCapturedPiecesBlue = new ArrayList<>();
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.MARSHALL, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.GENERAL, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.COLONEL, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.MAJOR, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.CAPTAIN, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.LIEUTENANT, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.SERGEANT, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.MINER, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.SCOUT, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.SPY, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.BOMB, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.FLAG, StrategoConstants.BLUE, 0));
        adapterBlue = new StrategoCapturedPieceAdapter(listCapturedPiecesBlue);
        recyclerViewBlue.setAdapter(adapterBlue);
    }

    public void paintBoard () {
        strategoViewBase.paintBoard(strategoControl, -1, Collections.emptyList());
    }

    public void initTimer () {
        textViewClockTimeTop = parent.findViewById(R.id.TextViewClockTimeTop);
        textViewClockTimeBottom = parent.findViewById(R.id.TextViewClockTimeBottom);
        m_timerHandler = new StrategoInnerHandler(this);
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                m_timerHandler.sendMessage(msg);
            }
        }, 1000, 1000);
    }

    public void initFightView () {
        fightPiece1 = parent.findViewById(R.id.fightPiece1);
        fightPiece2 = parent.findViewById(R.id.fightPiece2);
        fightPiece1Indicator = parent.findViewById(R.id.fightPiece1Indicator);
        fightPiece2Indicator = parent.findViewById(R.id.fightPiece2Indicator);
    }

    public void showNext() {
        viewAnimator.showNext();
    }

    public void showPrevious() {
        viewAnimator.showPrevious();
    }

    public void updateStatus () {
        updateFight ();
        updateCapturedPieces (StrategoConstants.RED, strategoControl.getBoard().getCapturedPiecesRed());
        updateCapturedPieces (StrategoConstants.BLUE, strategoControl.getBoard().getCapturedPiecesBlue());
    }

    private String formatTime(long msec) {
        return  String.format("%02d:%02d", (int) (Math.floor(msec / 60000)), ((int) (msec / 1000) % 60));
    }

    public StrategoControl getStrategoControl() {
        return strategoControl;
    }

    private void updateFight () {
        List<HistoryPiece> historyMoves = strategoControl.getBoard().getHistoryMoves();
        List<HistoryPiece> fights = new ArrayList<>();
        for (HistoryPiece h : historyMoves) {
            if (h.fightStatus != PieceFightStatus.NO_FIGHT && h.fightStatus != PieceFightStatus.FLAG_CAPTURED) {
                fights.add(h);
            }
        }

        if (totalFighths != fights.size()) {
            // new fight
            totalFighths = fights.size();
            HistoryPiece lastFight = fights.get(fights.size()-1);
            Log.d(TAG, "Last Pieces fighting:" + lastFight.toString());

            fightPiece1.setImageBitmap(StrategoImageView.arrPieceBitmaps[lastFight.pieceFrom.getPlayer()][lastFight.pieceFrom.getPieceEnum().getId()]);
            fightPiece2.setImageBitmap(StrategoImageView.arrPieceBitmaps[lastFight.pieceTo.getPlayer()][lastFight.pieceTo.getPieceEnum().getId()]);

            if (lastFight.fightStatus == PieceFightStatus.PIECE1_WIN) {
                fightPiece1Indicator.setImageDrawable(drawableFightWin);
                fightPiece2Indicator.setImageDrawable(drawableFightLost);
            } else if (lastFight.fightStatus == PieceFightStatus.PIECE2_WIN) {
                fightPiece1Indicator.setImageDrawable(drawableFightLost);
                fightPiece2Indicator.setImageDrawable(drawableFightWin);
            } else if (lastFight.fightStatus == PieceFightStatus.TIE) {
                fightPiece1Indicator.setImageDrawable(drawableFightLost);
                fightPiece2Indicator.setImageDrawable(drawableFightLost);
            } else {
                // should not happen
                fightPiece1Indicator.setImageDrawable(null);
                fightPiece2Indicator.setImageDrawable(null);
            }

            viewAnimator.setDisplayedChild(2);

            // Delay of 5 seconds
            int delayMillis = 2000;

            // Using Handler to delay execution
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Code to execute after delay
                viewAnimator.setDisplayedChild(1);
            }, delayMillis);
        }

    }

    private void updateCapturedPieces(int player, Map<PieceEnum, Integer> mapCapturedPieces) {
        if (mapCapturedPieces == null) {
            return;
        }

        List<CapturedPieceItem> temporary = StrategoConstants.RED == player ? listCapturedPiecesRed : listCapturedPiecesBlue;
        StrategoCapturedPieceAdapter temporaryAdapter = StrategoConstants.RED == player ? adapterRed : adapterBlue;

        boolean changes=false;
        for (CapturedPieceItem capturePiece : temporary) {
            if (mapCapturedPieces.containsKey(capturePiece.getPieceEnum())) {
                Integer total = mapCapturedPieces.get(capturePiece.getPieceEnum());
                if (total!=null && total >0 && capturePiece.getCaptured() != total) {
                    capturePiece.setCaptured(total);
                    if (capturePiece.getPieceEnum().maxLives() == total) {
                        capturePiece.setDead(true);
                    }
                    changes=true;
                }
            }
        }
        if (changes) {
            temporaryAdapter.notifyDataSetChanged();
        }
    }

    public int getPlayer() {
        return player;
    }
}
