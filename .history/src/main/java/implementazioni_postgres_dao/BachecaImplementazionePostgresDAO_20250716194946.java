package implementazioni_postgres_dao;

import database.DBConnessione;
import model.Ordinamento;
import model.Titolo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BachecaImplementazionePostgresDAO implements dao.BachecaDAO{

    private Connection connection;

    public BachecaImplementazionePostgresDAO(){
        try{
            connection = DBConnessione.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescrizioneBacheca(String emailUtente, Titolo titolo){
        try{
            String sql = "SELECT \"descrizione\" FROM \"Bacheca\" WHERE \"emailUtente\" = ? AND \"titolo\" = ?::titolo";
            PreparedStatement descrizionePS = connection.prepareStatement(sql);

            descrizionePS.setString(1, emailUtente);
            descrizionePS.setString(2, titolo.name());

            ResultSet rs = descrizionePS.executeQuery();

            if(rs.next()){
                return rs.getString("descrizione");
            } else {
                return null;
            }

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Ordinamento getOrdinamentoBacheca (String emailUtente, Titolo titolo){
        try{
            String sql = "SELECT ordinamento FROM \"Bacheca\" WHERE \"emailUtente\" = ? AND \"titolo\" = ?::titolo";
            PreparedStatement ordinamentoPS = connection.prepareStatement(sql);

            ordinamentoPS.setString(1, emailUtente);
            ordinamentoPS.setString(2, titolo.name());

            ResultSet rs = ordinamentoPS.executeQuery();
            if(rs.next()){
                String ordinamento = rs.getString("ordinamento");
                return Ordinamento.valueOf(ordinamento);
            } else {
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void salvaDescrizioneBacheca (String emailUtente, Titolo titolo, String descrizione){
        try{
            String sql = "UPDATE \"Bacheca\" SET \"descrizione\" = ? WHERE \"emailUtente\" = ? AND \"titolo\" = ?::titolo";

            PreparedStatement salvabPS = connection.prepareStatement(sql);

            salvabPS.setString(1, descrizione);
            salvabPS.setString(2, emailUtente);
            salvabPS.setString(3, titolo.name());

            salvabPS.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void salvaOrdinamentoBacheca (String emailUtente, Titolo titolo, Ordinamento ordinamento){
        try{
            String sql = "UPDATE \"Bacheca\" SET ordinamento = ? WHERE \"emailUtente\" = ? AND \"titolo\" = ?::titolo";

            PreparedStatement salvaoPS = connection.prepareStatement(sql);

            salvaoPS.setString(1, ordinamento.name());
            salvaoPS.setString(2, emailUtente);
            salvaoPS.setString(3, titolo.name());

            salvaoPS.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}