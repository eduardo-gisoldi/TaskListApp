package com.example.tasklistapp

// Importação das bibliotecas necessárias para manipular o banco de dados SQLite.
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Classe que auxilia na manipulação do banco de dados de tarefas.
class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "tasks.db", null, 1) {

    // Método chamado para criar o banco de dados quando ele é criado pela primeira vez.
    override fun onCreate(db: SQLiteDatabase?) {
        // Cria a tabela de tarefas com um campo 'id' como chave primária e um campo 'name' para o nome da tarefa.
        db?.execSQL("CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)")
    }

    // Método chamado quando a versão do banco de dados é alterada (atualização do banco).
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Exclui a tabela existente se ela existir.
        db?.execSQL("DROP TABLE IF EXISTS tasks")
        // Cria a tabela novamente após a exclusão.
        onCreate(db)
    }

    // Método para adicionar uma nova tarefa ao banco de dados.
    fun addTask(name: String) {
        // Abre o banco de dados em modo de escrita.
        val db = writableDatabase
        // Cria um objeto ContentValues para armazenar os dados da tarefa.
        val values = ContentValues()
        values.put("name", name) // Adiciona o nome da tarefa ao ContentValues.
        // Insere a nova tarefa na tabela 'tasks'.
        db.insert("tasks", null, values)
        // Fecha o banco de dados após a inserção.
        db.close()
    }

    // Método para obter todas as tarefas do banco de dados.
    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        // Abre o banco de dados em modo de leitura.
        val db = readableDatabase
        // Executa uma consulta para obter todas as tarefas da tabela.
        val cursor = db.rawQuery("SELECT * FROM tasks", null)

        // Verifica se o cursor contém resultados.
        if (cursor.moveToFirst()) {
            do {
                // Obtém o ID e o nome da tarefa do cursor.
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                // Adiciona a tarefa à lista de tarefas.
                tasks.add(Task(id, name))
            } while (cursor.moveToNext()) // Move para a próxima linha do cursor.
        }

        // Fecha o cursor e o banco de dados após a consulta.
        cursor.close()
        db.close()
        // Retorna a lista de tarefas.
        return tasks
    }

    // Método para atualizar o nome de uma tarefa existente no banco de dados.
    fun updateTask(id: Int, name: String) {
        // Abre o banco de dados em modo de escrita.
        val db = writableDatabase
        // Cria um objeto ContentValues para armazenar os novos dados da tarefa.
        val values = ContentValues()
        values.put("name", name) // Adiciona o novo nome da tarefa.
        // Atualiza a tarefa na tabela 'tasks' com base no ID da tarefa.
        db.update("tasks", values, "id = ?", arrayOf(id.toString()))
        // Fecha o banco de dados após a atualização.
        db.close()
    }

    // Método para excluir uma tarefa do banco de dados.
    fun deleteTask(id: Int): Boolean {
        // Abre o banco de dados em modo de escrita.
        val db = writableDatabase
        // Exclui a tarefa da tabela 'tasks' com base no ID da tarefa e retorna o número de linhas excluídas.
        val rowsDeleted = db.delete("tasks", "id = ?", arrayOf(id.toString()))
        // Fecha o banco de dados após a exclusão.
        db.close()
        // Retorna true se pelo menos uma linha foi excluída, caso contrário, retorna false.
        return rowsDeleted > 0
    }
}
