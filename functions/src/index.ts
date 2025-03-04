/**
 * Import function triggers from their respective submodules:
 * https://firebase.google.com/docs/functions
 */
import * as functions from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import {google} from "googleapis";

// Inicializar Firebase Admin
admin.initializeApp();

// ID de tu Google Sheets (cópialo desde la URL del documento)
const SHEET_ID = "17cEPfdBHfoaLyUHxk1pE0XOEJPV_Stys99Bi7cDXyOI";

// Variable para almacenar el cliente de Sheets una vez inicializado.
let sheetsClient: ReturnType<typeof google.sheets> | null = null;

/**
 * Cloud Function que exporta datos a Google Sheets.
 */
async function getSheetsClient() {
  if (sheetsClient) return sheetsClient;

  const credentialsBase64 = process.env.GOOGLE_CREDENTIALS_BASE64;
  if (!credentialsBase64) {
    throw new Error("Las credenciales de Google Sheets no están definidas.");
  }
  const credentials = JSON.parse(
    Buffer.from(credentialsBase64, "base64").toString()
  );

  const auth = new google.auth.GoogleAuth({
    credentials,
    scopes: ["https://www.googleapis.com/auth/spreadsheets"],
  });
  sheetsClient = google.sheets({version: "v4", auth});
  return sheetsClient;
}

// Cloud Function para exportar los turnos de un día específico.
// Se especifica el secreto para que se inyecte en el entorno de la función.
export const exportMealsToSheets = functions.onRequest(
  {secrets: ["GOOGLE_CREDENTIALS_BASE64"]},
  async (req, res) => {
    try {
      const {date} = req.query;
      if (!date) {
        res.status(400).send("Falta la fecha (date) en la consulta.");
        return;
      }

      const db = admin.firestore();
      const usersSnapshot = await db.collection("users").get();

      const mealData: string[][] = [["Usuario", "Desayuno", "Comida", "Cena"]];

      for (const userDoc of usersSnapshot.docs) {
        const userData = userDoc.data();
        const weekSnapshot = await userDoc.ref
          .collection("weeks")
          .doc(date as string)
          .get();

        if (weekSnapshot.exists) {
          const meals = weekSnapshot.data()?.meals || {};
          mealData.push([
            userData.name || "Sin nombre",
            meals["Monday"]?.["desayuno"] || "-",
            meals["Monday"]?.["comida"] || "-",
            meals["Monday"]?.["cena"] || "-",
          ]);
        }
      }

      // Obtener o inicializar el cliente de Sheets
      const sheets = await getSheetsClient();

      // Escribir en Google Sheets
      await sheets.spreadsheets.values.update({
        spreadsheetId: SHEET_ID,
        range: "A1", // Escribir desde la celda A1
        valueInputOption: "RAW",
        requestBody: {values: mealData},
      });

      res.status(200).send(`Datos exportados para el día ${date}`);
    } catch (error) {
      console.error("Error exportando a Sheets:", error);
      res.status(500).send("Error interno");
    }
  }
);
