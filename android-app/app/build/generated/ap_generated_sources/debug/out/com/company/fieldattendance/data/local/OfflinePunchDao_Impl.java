package com.company.fieldattendance.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class OfflinePunchDao_Impl implements OfflinePunchDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OfflinePunch> __insertionAdapterOfOfflinePunch;

  private final EntityDeletionOrUpdateAdapter<OfflinePunch> __deletionAdapterOfOfflinePunch;

  public OfflinePunchDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOfflinePunch = new EntityInsertionAdapter<OfflinePunch>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `offline_punches` (`id`,`employeeId`,`latitude`,`longitude`,`accuracy`,`faceStatus`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final OfflinePunch entity) {
        statement.bindLong(1, entity.id);
        if (entity.employeeId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.employeeId);
        }
        if (entity.latitude == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.latitude);
        }
        if (entity.longitude == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.longitude);
        }
        if (entity.accuracy == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.accuracy);
        }
        if (entity.faceStatus == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.faceStatus);
        }
        statement.bindLong(7, entity.timestamp);
      }
    };
    this.__deletionAdapterOfOfflinePunch = new EntityDeletionOrUpdateAdapter<OfflinePunch>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `offline_punches` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final OfflinePunch entity) {
        statement.bindLong(1, entity.id);
      }
    };
  }

  @Override
  public void insertPunch(final OfflinePunch punch) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfOfflinePunch.insert(punch);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deletePunches(final List<OfflinePunch> punches) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfOfflinePunch.handleMultiple(punches);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<OfflinePunch> getAllPunches() {
    final String _sql = "SELECT * FROM offline_punches";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEmployeeId = CursorUtil.getColumnIndexOrThrow(_cursor, "employeeId");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
      final int _cursorIndexOfFaceStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "faceStatus");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final List<OfflinePunch> _result = new ArrayList<OfflinePunch>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final OfflinePunch _item;
        _item = new OfflinePunch();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfEmployeeId)) {
          _item.employeeId = null;
        } else {
          _item.employeeId = _cursor.getString(_cursorIndexOfEmployeeId);
        }
        if (_cursor.isNull(_cursorIndexOfLatitude)) {
          _item.latitude = null;
        } else {
          _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        }
        if (_cursor.isNull(_cursorIndexOfLongitude)) {
          _item.longitude = null;
        } else {
          _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        }
        if (_cursor.isNull(_cursorIndexOfAccuracy)) {
          _item.accuracy = null;
        } else {
          _item.accuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
        }
        if (_cursor.isNull(_cursorIndexOfFaceStatus)) {
          _item.faceStatus = null;
        } else {
          _item.faceStatus = _cursor.getString(_cursorIndexOfFaceStatus);
        }
        _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
