package de.arvato.stratego;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StrategoView {

    public static final String TAG = "StrategoView";

    private StrategoViewBase strategoViewBase;
    private StrategoActivity parent;
    private LayoutInflater inflater;
    private StrategoControl strategoControl;
    private ViewAnimator viewAnimator;
    private StrategoCapturedImageView[][] arrImageCaptured;
    private TextView[][] arrTextCaptured;
    private int selectedPos;
    private List<Integer> nextMovements;
    private int player;

    public StrategoView(Activity activity) {
        super();
        parent = (StrategoActivity) activity;
        strategoViewBase = new StrategoViewBase(activity);
        inflater = (LayoutInflater) parent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        strategoControl = new StrategoControl();
        selectedPos=-1;
        player = StrategoConstants.RED;
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

        viewAnimator = (ViewAnimator) parent.findViewById(R.id.ViewAnimatorMain);
        if (viewAnimator != null) {
            viewAnimator.setOutAnimation(parent, R.anim.slide_left);
            viewAnimator.setInAnimation(parent, R.anim.slide_right);
        }

        //View bottomPlayLayoutView = parent.findViewById(R.id.bottomPlayLayout);

        strategoControl.randomPieces(player);

        initCapturedImages ();

        initDistributePieces () ;

        paintBoard();
    }



    public void handleClick(int index) {
        Log.d(TAG, "handleClick at index:" + index);
        if (strategoControl.selectPiece(index)) {
            Log.d (TAG, "Piece at position:" + index + " has been selected.");
            nextMovements = strategoControl.getPossibleMovements (index);
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
                strategoControl.movePiece(index);
                selectedPos = -1;
                nextMovements = Collections.emptyList();
            }
        }
        strategoViewBase.paintBoard(strategoControl, selectedPos, nextMovements);
        updateStatus();
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
        Button bPlayerRandom = parent.findViewById(R.id.PlayerRandom);

        bPlayerReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = strategoControl.startGame(player);
                if (!b) {
                    Toast.makeText(parent.getApplicationContext(), "Fill all pieces!", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO Game can start
                }

            }
        }
        );

        bPlayerRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strategoControl.randomPieces(player);
                paintBoard ();
            }
        }
        );
    }

    private void initCapturedImages() {
        arrImageCaptured = new StrategoCapturedImageView[2][12];
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MARSHALL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedMarshall);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MARSHALL.getId()].initBitmap("mariscal_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.GENERAL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedGeneral);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.GENERAL.getId()].initBitmap("general_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.COLONEL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedColonel);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.COLONEL.getId()].initBitmap("coronel_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.LIEUTENANT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedLieutenant);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.LIEUTENANT.getId()].initBitmap("lituant_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.CAPTAIN.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedCaptain);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.CAPTAIN.getId()].initBitmap("capitan_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MAJOR.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedMajor);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MAJOR.getId()].initBitmap("major_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SERGEANT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedSergeant);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SERGEANT.getId()].initBitmap("sargento_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MINER.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedMiner);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MINER.getId()].initBitmap("minero_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SCOUT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedScout);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SCOUT.getId()].initBitmap("scout_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SPY.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedSpy);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SPY.getId()].initBitmap("spy_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.BOMB.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedBomb);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.BOMB.getId()].initBitmap("bomb_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.FLAG.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedFlag);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.FLAG.getId()].initBitmap("bandera_red.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MARSHALL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueMarshall);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MARSHALL.getId()].initBitmap("mariscal_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.GENERAL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueGeneral);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.GENERAL.getId()].initBitmap("general_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.COLONEL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueColonel);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.COLONEL.getId()].initBitmap("coronel_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.LIEUTENANT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueLieutenant);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.LIEUTENANT.getId()].initBitmap("lituant_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.CAPTAIN.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueCaptain);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.CAPTAIN.getId()].initBitmap("capitan_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MAJOR.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueMajor);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MAJOR.getId()].initBitmap("major_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SERGEANT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueSergeant);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SERGEANT.getId()].initBitmap("sargento_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MINER.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueMiner);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MINER.getId()].initBitmap("minero_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SCOUT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueScout);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SCOUT.getId()].initBitmap("scout_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SPY.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueSpy);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SPY.getId()].initBitmap("spy_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.BOMB.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueBomb);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.BOMB.getId()].initBitmap("bomb_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.FLAG.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueFlag);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.FLAG.getId()].initBitmap("bandera_blue.png");


        arrTextCaptured = new TextView[2][12];
        arrTextCaptured[StrategoConstants.RED][PieceEnum.MARSHALL.getId()] =  parent.findViewById(R.id.TextViewCapturedRedMarshall);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.GENERAL.getId()] =  parent.findViewById(R.id.TextViewCapturedRedGeneral);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.COLONEL.getId()] =  parent.findViewById(R.id.TextViewCapturedRedColonel);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.LIEUTENANT.getId()] =  parent.findViewById(R.id.TextViewCapturedRedLieutenant);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.CAPTAIN.getId()] =  parent.findViewById(R.id.TextViewCapturedRedCaptain);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.MAJOR.getId()] =  parent.findViewById(R.id.TextViewCapturedRedMajor);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.SERGEANT.getId()] =  parent.findViewById(R.id.TextViewCapturedRedSergeant);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.MINER.getId()] =  parent.findViewById(R.id.TextViewCapturedRedMiner);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.SCOUT.getId()] =  parent.findViewById(R.id.TextViewCapturedRedScout);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.SPY.getId()] =  parent.findViewById(R.id.TextViewCapturedRedSpy);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.BOMB.getId()] =  parent.findViewById(R.id.TextViewCapturedRedBomb);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.FLAG.getId()] =  parent.findViewById(R.id.TextViewCapturedRedFlag);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.MARSHALL.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueMarshall);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.GENERAL.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueGeneral);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.COLONEL.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueColonel);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.LIEUTENANT.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueLieutenant);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.CAPTAIN.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueCaptain);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.MAJOR.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueMajor);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.SERGEANT.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueSergeant);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.MINER.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueMiner);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.SCOUT.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueScout);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.SPY.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueSpy);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.BOMB.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueBomb);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.FLAG.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueFlag);

        for (int i=0;i<2;i++) {
            for (int j=0;j<12;j++) {
                arrImageCaptured[i][j].setVisibility(View.INVISIBLE);
                arrTextCaptured[i][j].setVisibility(View.INVISIBLE);
            }
        }
    }

    public void paintBoard () {
        strategoViewBase.paintBoard(strategoControl, -1, Collections.emptyList());
    }

    public void showNext() {
        viewAnimator.showNext();
    }

    public void showPrevious() {
        viewAnimator.showPrevious();
    }

    public void updateStatus () {
        updateCapturedPieces (StrategoConstants.RED, strategoControl.getCapturedPiecesRed());
        updateCapturedPieces (StrategoConstants.BLUE, strategoControl.getCapturedPiecesBlue());
    }

    private void updateCapturedPieces(int player, Map<PieceEnum, Integer> capturedPieces) {
        for (Map.Entry<PieceEnum, Integer> entry : capturedPieces.entrySet()) {
            arrImageCaptured[player][entry.getKey().getId()].setVisibility(View.VISIBLE);
            arrTextCaptured[player][entry.getKey().getId()].setVisibility(View.VISIBLE);
            arrTextCaptured[player][entry.getKey().getId()].setText(String.valueOf(entry.getValue()));
        }
    }
}
