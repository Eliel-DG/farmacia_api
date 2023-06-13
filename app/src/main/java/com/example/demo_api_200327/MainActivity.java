package com.example.demo_api_200327;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnGuardar;
    private Button btnBuscar;
    private Button btnActualizar;
    private Button btnEliminar;
    private EditText etCodigoBaras;
    private EditText etDescripcion;
    private EditText etMarca;
    private EditText etPrecioCompra;
    private EditText etPrecioVenta;
    private EditText etExistencia;
    private ListView lvFarmacos;
    private RequestQueue colaPeticiones;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<String> origenDatos = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private String url = "http://192.168.1.77:3300/";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGuardar = findViewById(R.id.btnSave);
        btnActualizar = findViewById(R.id.btnUpdate);
        btnBuscar = findViewById(R.id.btnSearch);
        btnEliminar = findViewById(R.id.btnDelete);
        etCodigoBaras = findViewById(R.id.etCodigoBarras);
        etDescripcion = findViewById(R.id.etDescripcion);
        etMarca = findViewById(R.id.etMarca);
        etPrecioCompra = findViewById(R.id.etprecioCompra);
        etPrecioVenta = findViewById(R.id.etprecioVenta);
        etExistencia = findViewById(R.id.etExistencias);
        lvFarmacos = findViewById(R.id.lvFarmacos);
        colaPeticiones = Volley.newRequestQueue(this);
        listFarmacos();


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest peticion = new JsonObjectRequest(
                        Request.Method.GET,
                        url + etCodigoBaras.getText().toString(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.has("status"))
                                    Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                else {
                                    try {
                                        etDescripcion.setText(response.getString("descripcion"));
                                        etMarca.setText(response.getString("marca"));
                                        etPrecioCompra.setText(String.valueOf(response.getInt("preciocompra")));
                                        etPrecioVenta.setText(String.valueOf(response.getInt("precioventa")));
                                        etExistencia.setText(String.valueOf(response.getInt("existencias")));

                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(peticion);
                listFarmacos();
                adapter.notifyDataSetChanged();

            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject farmacos = new JSONObject();
                try {
                    farmacos.put("codigobarras", etCodigoBaras.getText().toString());
                    farmacos.put("descripcion", etDescripcion.getText().toString());
                    farmacos.put("marca", etMarca.getText().toString());
                    farmacos.put("preciocompra", Float.parseFloat(etPrecioCompra.getText().toString()));
                    farmacos.put("precioventa", Float.parseFloat(etPrecioVenta.getText().toString()));
                    farmacos.put("existencias", Float.parseFloat(etExistencia.getText().toString()));
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url + "insert/",
                        farmacos,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("Producto insertado")) {
                                        Toast.makeText(MainActivity.this, "Producto insertado con EXITO!", Toast.LENGTH_SHORT).show();
                                        etCodigoBaras.setText("");
                                        etDescripcion.setText("");
                                        etMarca.setText("");
                                        etPrecioCompra.setText("");
                                        etPrecioVenta.setText("");
                                        etExistencia.setText("");
                                        adapter.clear();
                                        lvFarmacos.setAdapter(adapter);
                                        listFarmacos();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(jsonObjectRequest);
                listFarmacos();
                adapter.notifyDataSetChanged();

            }
        });


        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCodigoBaras.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Primero use el BOTÓN BUSCAR!", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject farmacos = new JSONObject();
                    try {
                        farmacos.put("codigobarras", etCodigoBaras.getText().toString());

                        if (!etDescripcion.getText().toString().isEmpty()) {
                            farmacos.put("descripcion", etDescripcion.getText().toString());
                        }

                        if (!etMarca.getText().toString().isEmpty()) {
                            farmacos.put("marca", etMarca.getText().toString());
                        }

                        if (!etPrecioCompra.getText().toString().isEmpty()) {
                            farmacos.put("preciocompra", Float.parseFloat(etPrecioCompra.getText().toString()));
                        }

                        if (!etPrecioVenta.getText().toString().isEmpty()) {
                            farmacos.put("precioventa", Float.parseFloat(etPrecioVenta.getText().toString()));
                        }

                        if (!etExistencia.getText().toString().isEmpty()) {
                            farmacos.put("existencias", Float.parseFloat(etExistencia.getText().toString()));
                        }

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    JsonObjectRequest actualizar = new JsonObjectRequest(
                            Request.Method.PUT,
                            url + "actualizar/" + etCodigoBaras.getText().toString(),
                            farmacos,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Producto actualizado")) {
                                            Toast.makeText(MainActivity.this, "Producto actualizado con EXITO!", Toast.LENGTH_SHORT).show();

                                        } else if (response.getString("status").equals("No encontrado")) {
                                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                        }


                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                    );
                    colaPeticiones.add(actualizar);
                    // Llamar a listProducts() después de realizar la eliminación
                    listFarmacos();
                    adapter.notifyDataSetChanged();

                    // Establecer los campos en blanco después de actualizar la vista
                    etCodigoBaras.setText("");
                    etDescripcion.setText("");
                    etMarca.setText("");
                    etPrecioCompra.setText("");
                    etPrecioVenta.setText("");
                    etExistencia.setText("");
                }
            }
        });


        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCodigoBaras.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Ingrese el código de barras", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.DELETE,
                            url + "borrar/" + etCodigoBaras.getText().toString(),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Producto eliminado")) {
                                            Toast.makeText(MainActivity.this, "Producto Eliminado con EXITO!", Toast.LENGTH_SHORT).show();
                                        } else if (response.getString("status").equals("Not Found")) {
                                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    colaPeticiones.add(jsonObjectRequest);
                    // Llamar a listProducts() después de realizar la actualización
                    listFarmacos();
                    adapter.notifyDataSetChanged();

                    // Establecer los campos en blanco después de actualizar la vista
                    etCodigoBaras.setText("");
                    etDescripcion.setText("");
                    etMarca.setText("");
                    etPrecioCompra.setText("");
                    etPrecioVenta.setText("");
                    etExistencia.setText("");
                }
            }
        });
    }
    protected void listFarmacos(){

        jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        origenDatos.clear();
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        for(int i = 0;i < response.length(); i++){
                            try {
                                String codigobarras =response.getJSONObject(i).getString("codigobarras");
                                String descripcion = response.getJSONObject(i).getString("descripcion") ;
                                String marca = response.getJSONObject(i).getString("marca");
                                origenDatos.add(codigobarras +"::" +descripcion+"::"+marca);
                            } catch (JSONException e) {
                            }
                        }
                        adapter = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, origenDatos);
                        lvFarmacos.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        colaPeticiones.add(jsonArrayRequest);
    }
}