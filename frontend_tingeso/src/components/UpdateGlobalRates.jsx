import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Card,
    CardContent,
    Typography,
    TextField,
    Button,
    Alert,
    CircularProgress,
    Divider
} from "@mui/material";
import { ArrowBack as ArrowBackIcon } from "@mui/icons-material";
import globalRatesService from "../services/globalrates.service";

const UpdateGlobalRates = () => {
    const navigate = useNavigate();
    
    const [searchMode, setSearchMode] = useState(true);
    const [searchValue, setSearchValue] = useState('');

    const [globalRate, setGlobalRate] = useState(null);
    const [editFormData, setEditFormData] = useState({
        dailyRateValue: ''
    });
    
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSearch = async () => {
        if (!searchValue.trim()) {
            setError('Por favor ingresa un valor para buscar');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const response = await globalRatesService.getGlobalRatesById(searchValue.trim());

            setGlobalRate(response.data);
            setEditFormData({
                dailyRateValue: response.data.dailyRateValue || ''
            });
            setSearchMode(false);
            setError('');
        } catch (err) {
            console.error('Error al buscar la tarifa global:', err);
            let errorMessage = 'Error al buscar la tarifa global';

            if (err.response?.status === 404) {
                errorMessage = `No se encontró la tarifa global con ID: ${searchValue}`;
            } else if (err.response?.data) {
                errorMessage = err.response.data;
            }
            
            setError(errorMessage);
            setGlobalRate(null);
        } finally {
            setLoading(false);
        }
    };

    const handleEditChange = (e) => {
        const { name, value } = e.target;
        setEditFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleUpdate = async () => {
        if (!editFormData.dailyRateValue) {
            setError('Por favor completa todos los campos');
            return;
        }

        // Convertir a número y validar
        const dailyRateValue = parseInt(editFormData.dailyRateValue);

        if (isNaN(dailyRateValue)) {
            setError('El valor de la tarifa debe ser un número válido');
            return;
        }

        if (dailyRateValue <= 0) {
            setError('El valor de la tarifa debe ser mayor a 0');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const updatedGlobalRate = {
                id: globalRate.id,
                rateName: globalRate.rateName,
                dailyRateValue: dailyRateValue
            };

            await globalRatesService.updateValueRate(globalRate.id, dailyRateValue);

            alert('Valor de la tarifa global actualizado exitosamente');
            navigate('/globalRates');
        } catch (err) {
            console.error('Error al actualizar:', err);
            
            let errorMessage = 'Error al actualizar el valor de la tarifa global';
            
            if (err.response?.data) {
                errorMessage = err.response.data;
            }
            
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    const handleBack = () => {
        setSearchMode(true);
        setGlobalRate(null);
        setSearchValue('');
        setError('');
    };

    return (
        <Box sx={{ p: 3 }}>
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate('/globalRates')}
                sx={{ 
                    mb: 3,
                    color: '#0A142E',
                    '&:hover': {
                        backgroundColor: 'rgba(10, 20, 46, 0.05)'
                    }
                }}
            >
                Volver a las tarifas globales
            </Button>

            <Card>
                <CardContent sx={{ p: 4 }}>
                    {searchMode ? (
                        <>
                            <Typography 
                                variant="h5" 
                                gutterBottom 
                                sx={{ color: '#0A142E', fontWeight: 'bold', mb: 3 }}
                            >
                                Buscar tarifa global para Actualizar
                            </Typography>

                            {error && (
                                <Alert severity="error" sx={{ mb: 3 }}>
                                    {error}
                                </Alert>
                            )}

                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                                <Box sx={{ display: 'flex', gap: 2 }}>
                                    <TextField
                                        fullWidth
                                        type="number"
                                        label="Ingresa ID"
                                        value={searchValue}
                                        onChange={(e) => setSearchValue(e.target.value)}
                                        onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                        placeholder="ej: 1"
                                        inputProps={{ min: 0 }}
                                    />
                                    <Button
                                        variant="outlined"
                                        onClick={() => setSearchValue('')}
                                        sx={{ minWidth: '120px' }}
                                    >
                                        Limpiar
                                    </Button>
                                </Box>

                                <Button
                                    variant="contained"
                                    onClick={handleSearch}
                                    disabled={!searchValue.trim() || loading}
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
                                    {loading ? <CircularProgress size={24} sx={{ color: '#FACC15' }} /> : 'Buscar'}
                                </Button>
                            </Box>
                        </>
                    ) : (
                        <>
                            <Typography 
                                variant="h5" 
                                gutterBottom 
                                sx={{ color: '#0A142E', fontWeight: 'bold', mb: 3 }}
                            >
                                Actualizar Tarifa Global
                            </Typography>

                            {error && (
                                <Alert severity="error" sx={{ mb: 3 }}>
                                    {error}
                                </Alert>
                            )}

                            {/* Datos de la tarifa global (solo lectura) */}
                            <Box sx={{ mb: 4, p: 2, backgroundColor: '#f5f5f5', borderRadius: 1 }}>
                                <Typography variant="h6" sx={{ mb: 2, color: '#0A142E' }}>
                                    Información de la Tarifa Global
                                </Typography>
                                
                                <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                                    <Box>
                                        <Typography variant="body2" sx={{ color: '#666', mb: 0.5 }}>
                                            <strong>ID:</strong>
                                        </Typography>
                                        <Typography variant="body1">{globalRate.id}</Typography>
                                    </Box>

                                    <Box>
                                        <Typography variant="body2" sx={{ color: '#666', mb: 0.5 }}>
                                            <strong>Nombre:</strong>
                                        </Typography>
                                        <Typography variant="body1">{globalRate.rateName}</Typography>
                                    </Box>
                                </Box>
                            </Box>

                            <Divider sx={{ my: 3 }} />

                            {/* Formulario de edición */}
                            <Typography variant="h6" sx={{ mb: 2, color: '#0A142E' }}>
                                Editar Valor de la Tarifa
                            </Typography>

                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    label="Valor de la Tarifa"
                                    name="dailyRateValue"
                                    value={editFormData.dailyRateValue}
                                    onChange={handleEditChange}
                                    required
                                    inputProps={{ min: 0, step: "1" }}
                                />

                                <Alert severity="info">
                                    No se pueden modificar: ID, nombre.
                                </Alert>

                                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                                    <Button
                                        variant="outlined"
                                        onClick={handleBack}
                                        disabled={loading}
                                    >
                                        Cancelar
                                    </Button>
                                    <Button
                                        variant="contained"
                                        onClick={handleUpdate}
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
                                        {loading ? <CircularProgress size={24} sx={{ color: '#FACC15' }} /> : 'Actualizar Tarifa'}
                                    </Button>
                                </Box>
                            </Box>
                        </>
                    )}
                </CardContent>
            </Card>
        </Box>
    );
};

export default UpdateGlobalRates;