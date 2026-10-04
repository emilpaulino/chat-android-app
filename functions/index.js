const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { getFirestore } = require("firebase-admin/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

const db = getFirestore(admin.app(), "chat-app");

exports.sendMessageNotification = onDocumentCreated(
    {
        document: "chats/{chatId}/messages/{messageId}",
        database: "chat-app"
    },
    async (event) => {

        const message = event.data.data();
        const senderId = message.senderId;
        const chatId = event.params.chatId;

        const chatSnapshot = await db
            .collection("chats")
            .doc(chatId)
            .get();

        if (!chatSnapshot.exists) {
            return;
        }

        const chatData = chatSnapshot.data();
        const participants = chatData.participants;

        let receiverId = null;

        for (const participantId of participants) {
            if (participantId !== senderId) {
                receiverId = participantId;
                break;
            }
        }

        if (receiverId === null) {
            return;
        }

        const receiverSnapshot = await db
            .collection("users")
            .doc(receiverId)
            .get();

        if (!receiverSnapshot.exists) {
            return;
        }

        const receiverData = receiverSnapshot.data();
        const token = receiverData.fcmToken;

        if (!token) {
            return;
        }

        const senderSnapshot = await db
            .collection("users")
            .doc(senderId)
            .get();

        let senderName = "Nuevo mensaje";

        if (senderSnapshot.exists) {
            const senderData = senderSnapshot.data();

            if (senderData.userName) {
                senderName = senderData.userName;
            }
        }

        let notificationBody = message.text;

        if (message.type === "image") {
            notificationBody = "Imagen";
        }

        await admin.messaging().send({
            token: token,
            notification: {
                title: senderName,
                body: notificationBody
            }
        });
    }
);