package com.example.kioskosnacks.Marshall.app;

public class PrintDataGlobal {

    static PrintDataGlobal printDataGlobal;

    private PrintDataGlobal() {

    }

    public String getMonto() {
        return Monto;
    }

    public void setMonto(String monto) {
        Monto = monto;
    }

    public String getComision() {
        return Comision;
    }

    public void setComision(String comision) {
        Comision = comision;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public long getIdTransaccion() {
        return IdTransaccion;
    }

    public void setIdTransaccion(long idTransaccion) {
        IdTransaccion = idTransaccion;
    }

    public String getReferencia() {
        return Referencia;
    }

    public void setReferencia(String referencia) {
        Referencia = referencia;
    }

    public String getNombreServicio() {
        return NombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        NombreServicio = nombreServicio;
    }

    public int getCodigoRespuesta() {
        return CodigoRespuesta;
    }

    public void setCodigoRespuesta(int codigoRespuesta) {
        CodigoRespuesta = codigoRespuesta;
    }

    public String getDescripcionRespuesta() {
        return DescripcionRespuesta;
    }

    public void setDescripcionRespuesta(String descripcionRespuesta) {
        DescripcionRespuesta = descripcionRespuesta;
    }

    public String getInstruccion1() {
        return Instruccion1;
    }

    public void setInstruccion1(String instruccion1) {
        Instruccion1 = instruccion1;
    }

    public String getInstruccion2() {
        return Instruccion2;
    }

    public void setInstruccion2(String instuccion2) {
        Instruccion2 = instuccion2;
    }

    String Monto;
    String Comision;
    String Total;
    String Referencia;
    String NombreServicio;
    long IdTransaccion;
    int CodigoRespuesta;
    String DescripcionRespuesta;
    String Instruccion1;
    String Instruccion2;


    public static PrintDataGlobal obtenerDatosPrinter() {
        if (printDataGlobal == null) {
            printDataGlobal = new PrintDataGlobal();
        }
        return printDataGlobal;
    }

}
