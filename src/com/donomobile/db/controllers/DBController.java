package com.donomobile.db.controllers;


import java.util.ArrayList;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.RemoteException;

import com.donomobile.db.provider.Table_Funds.FundsColumns;
import com.donomobile.domain.Cards;
import com.donomobile.utils.Encrypter;
import com.donomobile.utils.Logger;
import com.donomobile.web.rskybox.CreateClientLogTask;

public class DBController {
	
	public static synchronized void saveCreditCard(ContentProviderClient mProvider, Cards newCard) {
    	ContentValues values = newCard.getContentValues();
        try {
        	
        	
            mProvider.insert(FundsColumns.CONTENT_URI, values);
        } catch (RemoteException e) {
			(new CreateClientLogTask("DBController.saveCreditCard", "Exception Caught", "error", e)).execute();

        }catch (Exception e){

        }
    }
	
	public static synchronized void deleteCard(ContentProviderClient mProvider, int cardId) {
        try {
        	String whereClause = FundsColumns._ID + " =?";
            String[] whereArgs = { String.valueOf(cardId) };
    	    mProvider.delete(FundsColumns.CONTENT_URI, whereClause, whereArgs);        	
        } catch (RemoteException e) {
			(new CreateClientLogTask("DBController.deleteCard", "Exception Caught", "error", e)).execute();

        }		
	}
	
	public static synchronized void updateCardName(ContentProviderClient mProvider, int cardId, String cardName) {
        try {
        	String whereClause = FundsColumns._ID + " =?";
            String[] whereArgs = { String.valueOf(cardId) };
    	    ContentValues myValues = new ContentValues();
    	    myValues.put(FundsColumns.CARD_NAME, encrypt(cardName));
    	    mProvider.update(FundsColumns.CONTENT_URI, myValues, whereClause, whereArgs);
        } catch (RemoteException e) {
			(new CreateClientLogTask("DBController.updateCard", "Exception Caught", "error", e)).execute();

        }		
	}
	
	
	
	public static synchronized void clearCards(ContentProviderClient mProvider) {
        try {
    	    mProvider.delete(FundsColumns.CONTENT_URI, null, null);         	
        } catch (RemoteException e) {
			(new CreateClientLogTask("DBController.clearCards", "Exception Caught", "error", e)).execute();
        }		
	}

	public static synchronized ArrayList<Cards> getCards(ContentProviderClient mProvider) {
    	try {
			Cursor cursor = null;
			ArrayList<Cards> cards = new ArrayList<Cards>();
			try {
			    String whereClause = FundsColumns.NUMBER + " IS NOT NULL";
			    String[] whereArgs = null;
			    String sortOrder = FundsColumns._ID + " DESC";  // get the most recent card added first
			    cursor = mProvider.query(FundsColumns.CONTENT_URI, null, whereClause, whereArgs, sortOrder);
			} catch (RemoteException e) {
				(new CreateClientLogTask("DBController.getCards.internal", "Exception Caught", "error", e)).execute();

			    e.printStackTrace();
			     return null;
			}
			
			if(cursor!=null && cursor.moveToFirst()) {
			     do {
			    	 cards.add(new Cards(cursor));
			     } while (cursor.moveToNext());    
				cursor.close();
			}
			
			return cards;
		} catch (Exception e) {
			(new CreateClientLogTask("DBController.getCards", "Exception Caught", "error", e)).execute();
			return null;
		}
    }
	
	public static synchronized int getCardCount(ContentProviderClient mProvider) {
		try {
			Cursor countCursor = null;
			int count = 0;
			try {
				countCursor = mProvider.query(FundsColumns.CONTENT_URI, new String[] {"count(*) AS count"}, null, null, null);
			} catch (RemoteException e) {
				e.printStackTrace();
				return 0;
			}
			if(countCursor != null && countCursor.moveToFirst()) {
				count = countCursor.getInt(0);
			}
			countCursor.close();
			return count;
		} catch (Exception e) {
			(new CreateClientLogTask("DBController.getCardCount", "Exception Caught", "error", e)).execute();
			return 0;

		}
    }
	
	
	protected static Encrypter getEncrypter() {
		Encrypter encrypter = Encrypter.INSTANCE;
		if (encrypter == null || !encrypter.isReadyToRun()) {
			encrypter.setupEncrypter();
		}
		return encrypter;
	}


	protected static String encrypt(String data) {
		return getEncrypter().packString(data);
	}
	
}
