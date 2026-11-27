package com.agrogestion.registros.model;

import jakarta.persistence.*;

@Entity
@Table(name = "registros_terreno")
public class RegistroTerreno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long localId;
    private String predio;
    private String ubicacion;
    private String cuartel;
    private String cultivo;
    private String etapa;
    private Double ph;
    private Double ec;
    private Double nitrogeno;
    private Double fosforo;
    private Double potasio;
    private Double calcio;
    private Double magnesio;
    private Double latitud;
    private Double longitud;
    private Float precisionMetros;
    private Boolean gpsDesdeSensor;
    private String observacionesFoliares;
    private String recomendacion;
    private Long fechaMillis;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLocalId() { return localId; }
    public void setLocalId(Long localId) { this.localId = localId; }
    public String getPredio() { return predio; }
    public void setPredio(String predio) { this.predio = predio; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getCuartel() { return cuartel; }
    public void setCuartel(String cuartel) { this.cuartel = cuartel; }
    public String getCultivo() { return cultivo; }
    public void setCultivo(String cultivo) { this.cultivo = cultivo; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }
    public Double getEc() { return ec; }
    public void setEc(Double ec) { this.ec = ec; }
    public Double getNitrogeno() { return nitrogeno; }
    public void setNitrogeno(Double nitrogeno) { this.nitrogeno = nitrogeno; }
    public Double getFosforo() { return fosforo; }
    public void setFosforo(Double fosforo) { this.fosforo = fosforo; }
    public Double getPotasio() { return potasio; }
    public void setPotasio(Double potasio) { this.potasio = potasio; }
    public Double getCalcio() { return calcio; }
    public void setCalcio(Double calcio) { this.calcio = calcio; }
    public Double getMagnesio() { return magnesio; }
    public void setMagnesio(Double magnesio) { this.magnesio = magnesio; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public Float getPrecisionMetros() { return precisionMetros; }
    public void setPrecisionMetros(Float precisionMetros) { this.precisionMetros = precisionMetros; }
    public Boolean getGpsDesdeSensor() { return gpsDesdeSensor; }
    public void setGpsDesdeSensor(Boolean gpsDesdeSensor) { this.gpsDesdeSensor = gpsDesdeSensor; }
    public String getObservacionesFoliares() { return observacionesFoliares; }
    public void setObservacionesFoliares(String observacionesFoliares) { this.observacionesFoliares = observacionesFoliares; }
    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) { this.recomendacion = recomendacion; }
    public Long getFechaMillis() { return fechaMillis; }
    public void setFechaMillis(Long fechaMillis) { this.fechaMillis = fechaMillis; }
}