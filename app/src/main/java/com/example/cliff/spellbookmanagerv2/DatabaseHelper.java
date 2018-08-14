package com.example.cliff.spellbookmanagerv2;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SpellbookManagerDatabase.db";

    public static final String CHARACTERS_TABLE_NAME = "characters";
    public static final String CHARACTERS_COLUMN_CHARACTER_ID = "character_id";
    public static final String CHARACTERS_COLUMN_CHARACTER_NAME = "character_name";
    public static final String CHARACTERS_COLUMN_CHARACTER_PROFICIENCY_BONUS = "character_proficiency_bonus";
    public static final String CHARACTERS_COLUMN_CHARACTER_INTELLIGENCE = "character_intelligence";
    public static final String CHARACTERS_COLUMN_CHARACTER_WISDOM = "character_wisdom";
    public static final String CHARACTERS_COLUMN_CHARACTER_CHARISMA = "character_charisma";
    public static final String CHARACTERS_COLUMN_CHARACTER_CLASSES = "character_classes";

    public static final String SPELLBOOKS_TABLE_NAME = "spellbooks";
    public static final String SPELLBOOKS_COLUMN_SPELLBOOK_SPELL_ID = "spellbook_spell_id";
    public static final String SPELLBOOKS_COLUMN_SPELL_NAME = "spell_name";
    public static final String SPELLBOOKS_COLUMN_CORRESPONDING_SPELL_ID = "corresponding_spell_id";
    public static final String SPELLBOOKS_COLUMN_SPELL_BELONGS_TO_CHARACTER_ID = "spell_belongs_to_character_id";

    public static final String SPELLS_TABLE_NAME = "spells";
    public static final String SPELLS_COLUMN_SPELL_ID = "spell_id";
    public static final String SPELLS_COLUMN_SPELL_NAME = "spell_name";
    public static final String SPELLS_COLUMN_CASTING_TIME = "casting_time";
    public static final String SPELLS_COLUMN_COMPONENTS = "components";
    public static final String SPELLS_COLUMN_MATERIAL = "material";
    public static final String SPELLS_COLUMN_RITUAL = "ritual";
    public static final String SPELLS_COLUMN_CONCENTRATION = "concentration";
    public static final String SPELLS_COLUMN_DESCRIPTION = "description";
    public static final String SPELLS_COLUMN_DURATION = "duration";
    public static final String SPELLS_COLUMN_LEVEL = "level";
    public static final String SPELLS_COLUMN_SPELL_RANGE = "spell_range";
    public static final String SPELLS_COLUMN_SCHOOL = "school";
    public static final String SPELLS_COLUMN_BELONGS_TO_CLASSES = "belongs_to_classes";
    public static final String SPELLS_COLUMN_IS_SPELL_HOMEBREW = "is_spell_homebrew";
    public static final String SPELLS_COLUMN_HOMEBREW_ID = "homebrew_id";
    public static final String SPELLS_COLUMN_BELONGS_TO_ACCOUNT_ID = "belongs_to_account_id";

    private static final String CREATE_TABLE_CHARACTERS = "CREATE TABLE " + CHARACTERS_TABLE_NAME
            + "(" + CHARACTERS_COLUMN_CHARACTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CHARACTERS_COLUMN_CHARACTER_NAME + " TEXT,"
            + CHARACTERS_COLUMN_CHARACTER_PROFICIENCY_BONUS + " TEXT,"
            + CHARACTERS_COLUMN_CHARACTER_INTELLIGENCE + " TEXT,"
            + CHARACTERS_COLUMN_CHARACTER_WISDOM + " TEXT,"
            + CHARACTERS_COLUMN_CHARACTER_CHARISMA + " TEXT,"
            + CHARACTERS_COLUMN_CHARACTER_CLASSES + " TEXT" + ")";

    private static final String CREATE_TABLE_SPELLBOOKS = "CREATE TABLE " + SPELLBOOKS_TABLE_NAME
            + "(" + SPELLBOOKS_COLUMN_SPELLBOOK_SPELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SPELLBOOKS_COLUMN_SPELL_NAME + " TEXT,"
            + SPELLBOOKS_COLUMN_CORRESPONDING_SPELL_ID + " TEXT,"
            + SPELLBOOKS_COLUMN_SPELL_BELONGS_TO_CHARACTER_ID + " INTEGER" + ")";

    private static final String CREATE_TABLE_SPELLS = "CREATE TABLE " + SPELLS_TABLE_NAME
            + "(" + SPELLS_COLUMN_SPELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SPELLS_COLUMN_SPELL_NAME + " TEXT,"
            + SPELLS_COLUMN_CASTING_TIME + " TEXT,"
            + SPELLS_COLUMN_COMPONENTS + " TEXT,"
            + SPELLS_COLUMN_MATERIAL + " TEXT,"
            + SPELLS_COLUMN_RITUAL + " TEXT,"
            + SPELLS_COLUMN_CONCENTRATION + " TEXT,"
            + SPELLS_COLUMN_DESCRIPTION + " TEXT,"
            + SPELLS_COLUMN_DURATION + " TEXT,"
            + SPELLS_COLUMN_LEVEL + " TEXT,"
            + SPELLS_COLUMN_SPELL_RANGE + " TEXT,"
            + SPELLS_COLUMN_SCHOOL + " TEXT,"
            + SPELLS_COLUMN_BELONGS_TO_CLASSES + " TEXT,"
            + SPELLS_COLUMN_IS_SPELL_HOMEBREW + " TEXT,"
            + SPELLS_COLUMN_HOMEBREW_ID + " INTEGER,"
            + SPELLS_COLUMN_BELONGS_TO_ACCOUNT_ID + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_CHARACTERS);
        database.execSQL(CREATE_TABLE_SPELLBOOKS);
        database.execSQL(CREATE_TABLE_SPELLS);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE " + CHARACTERS_TABLE_NAME);
        database.execSQL("DROP TABLE " + SPELLBOOKS_TABLE_NAME);
        database.execSQL("DROP TABLE " + SPELLS_TABLE_NAME);
    }

    /*
        Character table related functions
     */
    public boolean createCharacter(String name, String proficiencyBonus, String intelligence, String wisdom, String charisma, String classes) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHARACTERS_COLUMN_CHARACTER_NAME, name);
        contentValues.put(CHARACTERS_COLUMN_CHARACTER_PROFICIENCY_BONUS, proficiencyBonus);
        contentValues.put(CHARACTERS_COLUMN_CHARACTER_INTELLIGENCE, intelligence);
        contentValues.put(CHARACTERS_COLUMN_CHARACTER_WISDOM, wisdom);
        contentValues.put(CHARACTERS_COLUMN_CHARACTER_CHARISMA, charisma);
        contentValues.put(CHARACTERS_COLUMN_CHARACTER_CLASSES, classes);

        database.insert(CHARACTERS_TABLE_NAME, null, contentValues);
        return true;
    }

    public void deleteCharacter(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(CHARACTERS_TABLE_NAME, CHARACTERS_COLUMN_CHARACTER_ID + " =  ?", new String[] {String.valueOf(id)});
    }

    public ArrayList<String> findAllCharacters() {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + CHARACTERS_TABLE_NAME, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int characterId = cursor.getInt(cursor.getColumnIndex(CHARACTERS_COLUMN_CHARACTER_ID));
            String characterName = cursor.getString(cursor.getColumnIndex(CHARACTERS_COLUMN_CHARACTER_NAME));
            arrayList.add(characterId + "|" + characterName);
            cursor.moveToNext();
        }

        cursor.close();

        return arrayList;
    }

    public ArrayList<String> findCharacterById(int id) {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + CHARACTERS_TABLE_NAME + " WHERE " + CHARACTERS_COLUMN_CHARACTER_ID + " = '" + id + "'", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                arrayList.add(cursor.getString(i));
            }

            cursor.moveToNext();
        }

        cursor.close();

        return arrayList;
    }

    public boolean updateCharacter(int id) {
        return true;
    }

    /*
        Spellbook table related functions
     */
    public boolean addToSpellbook(String spellname, int correspondingSpellId, int belongsToCharacterId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPELLBOOKS_COLUMN_SPELL_NAME, spellname);
        contentValues.put(SPELLBOOKS_COLUMN_CORRESPONDING_SPELL_ID, correspondingSpellId);
        contentValues.put(SPELLBOOKS_COLUMN_SPELL_BELONGS_TO_CHARACTER_ID, belongsToCharacterId);

        database.insert(SPELLBOOKS_TABLE_NAME, null, contentValues);
        return true;
    }

    public void deleteFromSpellbook(int spellId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(SPELLBOOKS_TABLE_NAME, SPELLBOOKS_COLUMN_SPELLBOOK_SPELL_ID + " =  ?", new String[] {String.valueOf(spellId)});
    }

    public ArrayList<String> findAllSpellsInCharacterSpellbook(int belongsToCharacterId) {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + SPELLBOOKS_TABLE_NAME + " WHERE " + SPELLBOOKS_COLUMN_SPELL_BELONGS_TO_CHARACTER_ID + " = '" + belongsToCharacterId + "'", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int spellbookId = cursor.getInt(cursor.getColumnIndex(SPELLBOOKS_COLUMN_SPELLBOOK_SPELL_ID));
            String spellName = cursor.getString(cursor.getColumnIndex(SPELLBOOKS_COLUMN_SPELL_NAME));
            int correspondingSpellId = cursor.getInt(cursor.getColumnIndex(SPELLBOOKS_COLUMN_CORRESPONDING_SPELL_ID));
            arrayList.add(spellbookId + "|" + spellName + "|" + correspondingSpellId + "|" + belongsToCharacterId);
            cursor.moveToNext();
        }

        cursor.close();

        return arrayList;
    }

    /*
        Spells table related functions
     */
    public boolean populateTableWithOfficial(String name, String castingTime, String components, String material,
                                 String ritual, String concentration, String description, String duration, String level, String spellRange,
                                 String school, String classes) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPELLS_COLUMN_SPELL_NAME , name);
        contentValues.put(SPELLS_COLUMN_CASTING_TIME , castingTime);
        contentValues.put(SPELLS_COLUMN_COMPONENTS , components);
        contentValues.put(SPELLS_COLUMN_MATERIAL , material);
        contentValues.put(SPELLS_COLUMN_RITUAL , ritual);
        contentValues.put(SPELLS_COLUMN_CONCENTRATION , concentration);
        contentValues.put(SPELLS_COLUMN_DESCRIPTION , description);
        contentValues.put(SPELLS_COLUMN_DURATION , duration);
        contentValues.put(SPELLS_COLUMN_LEVEL , level);
        contentValues.put(SPELLS_COLUMN_SPELL_RANGE , spellRange);
        contentValues.put(SPELLS_COLUMN_SCHOOL , school);
        contentValues.put(SPELLS_COLUMN_BELONGS_TO_CLASSES , classes);
        contentValues.put(SPELLS_COLUMN_IS_SPELL_HOMEBREW , "false");
        contentValues.put(SPELLS_COLUMN_HOMEBREW_ID, -1);
        contentValues.put(SPELLS_COLUMN_BELONGS_TO_ACCOUNT_ID , -1);

        database.insert(SPELLS_TABLE_NAME , null, contentValues);
        return true;
    }

    public boolean populateTableWithHomebrew(String name, String castingTime, String components, String material,
                                             String ritual, String concentration, String description, String duration, String level, String spellRange,
                                             String school, String classes, String isSpellHomebrew, int homebrewId, int belongsToAccountId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPELLS_COLUMN_SPELL_NAME , name);
        contentValues.put(SPELLS_COLUMN_CASTING_TIME , castingTime);
        contentValues.put(SPELLS_COLUMN_COMPONENTS , components);
        contentValues.put(SPELLS_COLUMN_MATERIAL , material);
        contentValues.put(SPELLS_COLUMN_RITUAL , ritual);
        contentValues.put(SPELLS_COLUMN_CONCENTRATION , concentration);
        contentValues.put(SPELLS_COLUMN_DESCRIPTION , description);
        contentValues.put(SPELLS_COLUMN_DURATION , duration);
        contentValues.put(SPELLS_COLUMN_LEVEL , level);
        contentValues.put(SPELLS_COLUMN_SPELL_RANGE , spellRange);
        contentValues.put(SPELLS_COLUMN_SCHOOL , school);
        contentValues.put(SPELLS_COLUMN_BELONGS_TO_CLASSES , classes);
        contentValues.put(SPELLS_COLUMN_IS_SPELL_HOMEBREW , isSpellHomebrew);
        contentValues.put(SPELLS_COLUMN_HOMEBREW_ID, homebrewId);
        contentValues.put(SPELLS_COLUMN_BELONGS_TO_ACCOUNT_ID , belongsToAccountId);

        database.insert(SPELLS_TABLE_NAME , null, contentValues);
        return true;
    }

    public ArrayList<String> findAllSpells() {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + SPELLS_TABLE_NAME , null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                row.append(cursor.getString(i));
                if (i < cursor.getColumnCount()-1) {
                    row.append("|");
                }
            }
            arrayList.add(row.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return arrayList;
    }

    public ArrayList<String> findAllSpellNamesAndIds() {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT " + SPELLS_COLUMN_SPELL_ID + ", " + SPELLS_COLUMN_SPELL_NAME + " FROM " + SPELLS_TABLE_NAME , null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                row.append(cursor.getString(i));
                if (i < cursor.getColumnCount()-1) {
                    row.append("|");
                }
            }
            arrayList.add(row.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return arrayList;
    }

    public ArrayList<String> findSearchedSpells(String regName) {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT " + SPELLS_COLUMN_SPELL_ID + ", " + SPELLS_COLUMN_SPELL_NAME + " FROM " + SPELLS_TABLE_NAME + " WHERE " +
                SPELLS_COLUMN_SPELL_NAME + " LIKE " + "'%" + regName + "%'", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                row.append(cursor.getString(i));
                if (i < cursor.getColumnCount()-1) {
                    row.append("|");
                }
            }
            arrayList.add(row.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return arrayList;
    }

    public ArrayList<String> findSpellByClass(String regClassName) {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT " + SPELLS_COLUMN_SPELL_ID + ", " + SPELLS_COLUMN_SPELL_NAME + " FROM " + SPELLS_TABLE_NAME + " WHERE " +
                SPELLS_COLUMN_BELONGS_TO_CLASSES + " LIKE " + "'%" + regClassName + "%'", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                row.append(cursor.getString(i));
                if (i < cursor.getColumnCount()-1) {
                    row.append("|");
                }
            }
            arrayList.add(row.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return arrayList;
    }

    public ArrayList<String> findSpellById(int id) {
        ArrayList<String> spellDetails = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + SPELLS_TABLE_NAME + " WHERE " +
                SPELLS_COLUMN_SPELL_ID + " = " + "'" + id + "'", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                spellDetails.add(cursor.getString(i));
            }
            cursor.moveToNext();
        }

        cursor.close();

        return spellDetails;
    }

    public boolean syncHomebrewContent(ArrayList<String> homebrewDatabaseRows) {
        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<String> localSpellList = findAllSpells();
        for (int i = 0; i < homebrewDatabaseRows.size(); i++) {
            boolean inDatabaseAlready = false;
            for (int j = 0; j < localSpellList.size(); j++) {
                if (!localSpellList.get(j).split("\\|")[13].equals("false")) {
                    int homebrewIdOnRemote = Integer.parseInt(homebrewDatabaseRows.get(i).split("\\|")[0]);
                    int homebrewCorrespondingIdOnLocal = Integer.parseInt(localSpellList.get(j).split("\\|")[14]);
                    if (homebrewCorrespondingIdOnLocal == homebrewIdOnRemote) {
                        String homebrewRemoteInfo[] = homebrewDatabaseRows.get(i).split("\\|");
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(SPELLS_COLUMN_SPELL_NAME , homebrewRemoteInfo[1]);
                        contentValues.put(SPELLS_COLUMN_CASTING_TIME , homebrewRemoteInfo[2]);
                        contentValues.put(SPELLS_COLUMN_COMPONENTS , homebrewRemoteInfo[3] );
                        contentValues.put(SPELLS_COLUMN_MATERIAL , homebrewRemoteInfo[4] );
                        contentValues.put(SPELLS_COLUMN_RITUAL , homebrewRemoteInfo[5]);
                        contentValues.put(SPELLS_COLUMN_CONCENTRATION , homebrewRemoteInfo[6]);
                        contentValues.put(SPELLS_COLUMN_DESCRIPTION , homebrewRemoteInfo[7]);
                        contentValues.put(SPELLS_COLUMN_DURATION , homebrewRemoteInfo[8] );
                        contentValues.put(SPELLS_COLUMN_LEVEL , homebrewRemoteInfo[9]);
                        contentValues.put(SPELLS_COLUMN_SPELL_RANGE , homebrewRemoteInfo[10]);
                        contentValues.put(SPELLS_COLUMN_SCHOOL , homebrewRemoteInfo[11]);
                        contentValues.put(SPELLS_COLUMN_BELONGS_TO_CLASSES , homebrewRemoteInfo[12]);
                        database.update(SPELLS_TABLE_NAME, contentValues, SPELLS_COLUMN_HOMEBREW_ID + " =  ?", new String[] {String.valueOf(homebrewIdOnRemote)});
                        inDatabaseAlready = true;
                    }
                }
            }

            if (!inDatabaseAlready) {
                String homebrewRemoteInfo[] = homebrewDatabaseRows.get(i).split("\\|");
                ContentValues contentValues = new ContentValues();
                contentValues.put(SPELLS_COLUMN_SPELL_NAME , homebrewRemoteInfo[1]);
                contentValues.put(SPELLS_COLUMN_CASTING_TIME , homebrewRemoteInfo[2]);
                contentValues.put(SPELLS_COLUMN_COMPONENTS , homebrewRemoteInfo[3] );
                contentValues.put(SPELLS_COLUMN_MATERIAL , homebrewRemoteInfo[4] );
                contentValues.put(SPELLS_COLUMN_RITUAL , homebrewRemoteInfo[5]);
                contentValues.put(SPELLS_COLUMN_CONCENTRATION , homebrewRemoteInfo[6]);
                contentValues.put(SPELLS_COLUMN_DESCRIPTION , homebrewRemoteInfo[7]);
                contentValues.put(SPELLS_COLUMN_DURATION , homebrewRemoteInfo[8] );
                contentValues.put(SPELLS_COLUMN_LEVEL , homebrewRemoteInfo[9]);
                contentValues.put(SPELLS_COLUMN_SPELL_RANGE , homebrewRemoteInfo[10]);
                contentValues.put(SPELLS_COLUMN_SCHOOL , homebrewRemoteInfo[11]);
                contentValues.put(SPELLS_COLUMN_BELONGS_TO_CLASSES , homebrewRemoteInfo[12]);
                contentValues.put(SPELLS_COLUMN_IS_SPELL_HOMEBREW , "true");
                contentValues.put(SPELLS_COLUMN_HOMEBREW_ID, Integer.parseInt(homebrewRemoteInfo[0]) );
                contentValues.put(SPELLS_COLUMN_BELONGS_TO_ACCOUNT_ID , Integer.parseInt(homebrewRemoteInfo[13]));
                database.insert(SPELLS_TABLE_NAME , null, contentValues);
            }
        }

        return true;
    }

    public boolean clearLocalHomebrewSpells() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + SPELLS_TABLE_NAME + " WHERE " + SPELLS_COLUMN_IS_SPELL_HOMEBREW + " != 'false'");
        return true;
    }
}
