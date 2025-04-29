package com.example.moncloaplus.screens.create_event

import com.example.moncloaplus.model.ActividadesColegiales
import com.example.moncloaplus.model.ClubesProfesionales
import com.example.moncloaplus.model.EventType

val eventTypeNameMap = mapOf(
    EventType.ACTIVIDAD_COLEGIAL to "Actividades colegiales",
    EventType.CLUBES_PROFESIONALES to "Clubes profesionales",
    EventType.DE_INTERES to "De interés"
)

val actividadesColegialesNameMap = mapOf(
    ActividadesColegiales.TERTULIAS_INVITADO to "Tertulias con invitado",
    ActividadesColegiales.AMOR_O_SEXO to "Amor o sexo",
    ActividadesColegiales.PROGRAMA_FOCO to "Programa FOCO",
    ActividadesColegiales.CULTURA to "Cultura",
    ActividadesColegiales.SOLIDARIDAD to "Solidaridad",
    ActividadesColegiales.DEPORTES to "Deportes",
    ActividadesColegiales.FORMACION_CRISTIANA to "Formación cristiana",
    ActividadesColegiales.EVENTOS to "Eventos"
)

val clubesProfesionalesNameMap = mapOf(
    ClubesProfesionales.MEDICINA to "Medicina",
    ClubesProfesionales.EMPRESA to "Empresa",
    ClubesProfesionales.DERECHO to "Derecho",
    ClubesProfesionales.INGENIERIA to "Ingeniería"
)
