import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    Box,
    Card,
    CardContent,
    Typography,
    TextField,
    Button,
    Alert,
    CircularProgress,
} from "@mui/material";
import globalRatesService from "../services/globalrates.service";

const AddGlobalRates = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        rateName: '',
        dailyRateValue: ''
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Validación de campos obligatorios
        if (!formData.rateName || !formData.dailyRateValue) {
            setError('Por favor completa todos los campos obligatorios');
            return;
        }

        // Convertir a números para validar
        const dailyRateValue = parseInt(formData.dailyRateValue);

        // Validar que sean números válidos
        if (isNaN(dailyRateValue)) {
            setError('Los valores numéricos no son válidos');
            return;
        }

        // Validar que sean mayores a 0
        if (dailyRateValue <= 0) {
            setError('Los valores numéricos deben ser mayores a 0');
            return;
        }

        setLoading(true);
        setError('');

        try {
            // Convertir el valor a número antes de enviar
            const dataToSend = {
                ...formData,
                dailyRateValue: dailyRateValue
            };
            await globalRatesService.create(dataToSend);

            alert('Tarifa global agregada exitosamente');
            navigate('/globalRates');
            // Limpiar formulario
            setFormData({
                rateName: '',
                dailyRateValue: ''
            });     
            
        } catch (err) {
            console.error('Error Completo:', err);

            let errorMessage = 'Error al guardar la tarifa global';
            
            if (err.response) {
                const data = err.response.data;
                if (typeof data === 'string') {
                    errorMessage = data;
                } else if (data.error) {
                    errorMessage = data.error;
                } else if (data.message) {
                    errorMessage = data.message;
                } else {
                    errorMessage = `Error: ${err.response.status} - ${err.response.statusText || 'Bad Request'}`;
                }
            } else if (err.request) {
                errorMessage = 'No se pudo conectar con el servidor';
            } else {
                errorMessage = err.message || 'Error al procesar la solicitud';
            }
            
            setError(errorMessage);
            
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Card>
                <CardContent sx={{ p: 4 }}>
                    <Typography 
                        variant="h5" 
                        gutterBottom 
                        sx={{ color: '#0A142E', fontWeight: 'bold', mb: 3 }}
                    >
                        Agregar Nueva Tarifa Global
                    </Typography>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}
                    >
                        <TextField
                            fullWidth
                            label="Nombre"
                            name="rateName"
                            value={formData.rateName}
                            onChange={handleChange}
                            required
                        />

                        <TextField
                            fullWidth
                            type="number"
                            label="Valor de tarifa diaria"
                            name="dailyRateValue"
                            value={formData.dailyRateValue}
                            onChange={handleChange}
                            required
                            inputProps={{ min: 0, step: "1" }}
                        />

                        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={() => setFormData({
                                    rateName: '',
                                    dailyRateValue: ''
                                })}
                                disabled={loading}
                            >
                                Limpiar
                            </Button>
                            <Button
                                type="submit"
                                variant="contained"
                                disabled={loading}
                                sx={{
                                    backgroundColor: '#0A142E',
                                    color: '#FACC15',
                                    '&:hover': {
                                        backgroundColor: '#1a2847'
                                    },
                                    '&:disabled': {
                                        backgroundColor: '#e0e0e0',
                                        color: '#9e9e9e'
                                    }
                                }}
                            >
                                {loading ? (
                                    <CircularProgress size={24} sx={{ color: '#FACC15' }} />
                                ) : (
                                    'Agregar Tarifa Global'
                                )}
                            </Button>
                        </Box>
                    </Box>
                </CardContent>
            </Card>
        </Box>
    );
};

export default AddGlobalRates;