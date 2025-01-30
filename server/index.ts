import * as functions from "firebase-functions/v2";
import * as admin from "firebase-admin";
import { FieldValue } from "firebase-admin/firestore";

admin.initializeApp();
const db = admin.firestore();

export const autoCancelLobby = functions.firestore.onDocumentCreated("lobbies/{lobbyId}", async (event) => {
    const lobbyId = event.params.lobbyId;

    setTimeout(async () => {
        try {
            const lobbySnapshot = await db.collection("lobbies")
                .doc(lobbyId)
                .get()

            if (lobbySnapshot.exists) {
                const lobbyStatus = lobbySnapshot.get("status") as string
                if (lobbyStatus === "WAITING") {
                    await db.collection("lobbies")
                        .doc(lobbyId)
                        .delete()
                    console.log("Lobby &s auto deleted", lobbyId)
                }
            }
        } catch (error) {
            console.error("Lobby could not be canceled:", error);
        }

    }, 20000)
})


// Veri tipini tanımlıyoruz.
type Guess = {
    lobbyId: string;
    playerId: number;
    word: string;
    isCorrect: boolean;
    isLastGuess: boolean;
};

// HTTP çağrısını tanımlıyoruz.
export const getGuess = functions.https.onCall(async (request) => {
    // Gelen veriyi kontrol ederek Guess tipine dönüştürüyoruz.
    const guess = request.data as Guess;

    // Validasyon (İsteğe bağlı, eksik veri veya hatalı veri kontrolü yapılır).
    if (!guess.lobbyId || typeof guess.isCorrect !== "boolean") {
        throw new functions.https.HttpsError(
            "invalid-argument",
            "Invalid or missing data"
        );
    }

    const lobbyRef = db.collection("lobbies").doc(guess.lobbyId);

    try {
        // Tahmin edilen kelimeyi guesses listesine ekliyoruz.
        if (guess.word) {
            await lobbyRef.update({
                guesses: FieldValue.arrayUnion(guess.word),
            });
        }

        // Eğer doğru tahminse, kazanan oyuncuyu kaydediyoruz.
        if (guess.isCorrect) {
            await lobbyRef.update({
                winner: guess.playerId
            });
        } else if (guess.isLastGuess) {
            await lobbyRef.update({
                winner: 0
            });
        } else {
            let turn: number
            if (guess.playerId === 1) {
                turn = 2
            } else {
                turn = 1
            }
            await lobbyRef.update("turn", turn)
        }

        // Başarılı işlem mesajı.
        return { success: true, message: "Guess processed successfully." };
    } catch (error) {
        console.error(`getGuess failed: ${error}`);

        // Hata durumunda kullanıcıya bilgi döndürülüyor.
        throw new functions.https.HttpsError(
            "internal",
            "Failed to process the guess."
        );
    }
});
