const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.onSolicitudCreated = functions.firestore
    .document('solicitudes/{solicitudId}')
    .onCreate(async (snap, context) => {
        const solicitud = snap.data();
        
        // Solo enviar notificación si es una solicitud pendiente
        if (solicitud.estado !== 'pendiente') {
            return null;
        }

        try {
            // Obtener todos los usuarios administradores del mismo negocio
            const adminUsers = await admin.firestore()
                .collection('usuarios')
                .where('rol', '==', 'ADMIN')
                .where('negocioId', '==', solicitud.negocioId)
                .get();

            const tokens = [];
            adminUsers.forEach(doc => {
                const userData = doc.data();
                if (userData.fcmToken) {
                    tokens.push(userData.fcmToken);
                }
            });

            if (tokens.length === 0) {
                console.log('No hay tokens FCM de administradores');
                return null;
            }

            // Crear mensaje de notificación
            const message = {
                notification: {
                    title: 'Nueva Solicitud de Empleo',
                    body: `${solicitud.nombreUsuario} quiere ser ${solicitud.rolSolicitado}`
                },
                data: {
                    tipo: 'solicitud_empleo',
                    solicitudId: context.params.solicitudId,
                    titulo: 'Nueva Solicitud de Empleo',
                    mensaje: `${solicitud.nombreUsuario} quiere ser ${solicitud.rolSolicitado}`
                },
                tokens: tokens
            };

            // Enviar notificación
            const response = await admin.messaging().sendMulticast(message);
            console.log('Notificaciones enviadas:', response.successCount, 'de', tokens.length);
            
            return response;
        } catch (error) {
            console.error('Error enviando notificación:', error);
            return null;
        }
    }); 