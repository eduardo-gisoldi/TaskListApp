package com.example.tasklistapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "tasks.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }

    fun addTask(name: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", name)
        db.insert("tasks", null, values)
        db.close()
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tasks", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                tasks.add(Task(id, name))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return tasks
    }


    fun updateTask(id: Int, name: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", name)
        db.update("tasks", values, "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteTask(id: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("tasks", "id = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted > 0 // Retorna true se alguma linha foi excluída
    }



}
