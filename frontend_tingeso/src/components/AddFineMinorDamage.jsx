import { useState, useEffect } from "react";
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
    Stepper,
    Step,
    StepLabel,
    Paper,
    List,
    ListItemButton,
    ListItemText,
    Divider
} from "@mui/material";
import { ArrowBack as ArrowBackIcon } from "@mui/icons-material";
import fineService from "../services/fine.service";
import loanService from "../services/loan.service";

const AddFineMinorDamage = () => {
    const navigate = useNavigate();

    // Estado del flujo
    const [activeStep, setActiveStep] = useState(0);

    // Datos generales
    const [allLoans, setAllLoans] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(true);
    const [error, setError] = useState('');

    // Paso 1: Seleccionar el préstamo
    const [searchTerm, setSearchTerm] = useState('');
    const [filteredLoans, setFilteredLoans] = useState([]);
    const [selectedLoan, setSelectedLoan] = useState(null);

    // Paso 2: colocar el valor del cargo por reparación menor(valor de la multa)
    const [fineValue, setFineValue] = useState('');

    // Función para formatear RUT - limpia y formatea correctamente
    const formatRut = (value) => {
        if (!value) return '';

        // Limpiar el RUT: eliminar puntos, guiones y espacios, dejar solo números y K
        const cleaned = value.toString().replace(/[^0-9kK]/g, '');
        if (!cleaned) return '';

        // Limitar a máximo 9 caracteres (8 dígitos + 1 dígito verificador)
        const limited = cleaned.slice(0, 9);

        // Si solo hay un carácter, devolverlo tal cual
        if (limited.length === 1) return limited;

        // Separar cuerpo y dígito verificador
        const body = limited.slice(0, -1);
        const dv = limited.slice(-1).toUpperCase();

        // Formatear el cuerpo con puntos
        const formattedBody = body.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

        // Retornar con guion
        return `${formattedBody}-${dv}`;
    };

    // Cargar datos iniciales
    useEffect(() => {
        const fetchData = async () => {
            setLoadingData(true);
            try {
                const loansRes = await loanService.getAll();

                setAllLoans(loansRes.data || []);
                setFilteredLoans(loansRes.data || []);
                setError('');
            } catch (err) {
                console.error('Error al cargar datos:', err);
                setError('Error al cargar préstamos');
            } finally {
                setLoadingData(false);
            }
        };

        fetchData();
    }, []);

    // PASO 1: Buscar préstamo por RUT del cliente con formateo en tiempo real
    const handleSearch = (e) => {
        const value = e.target.value;
        const formattedValue = formatRut(value);
        setSearchTerm(formattedValue);

        if (formattedValue.trim()) {
            // Buscar comparando RUTs sin formato
            const cleanSearch = formattedValue.replace(/[^0-9kK]/g, '').toLowerCase();
            const filtered = allLoans.filter(loan => {
                const cleanRut = (loan.customer?.rut || '').replace(/[^0-9kK]/g, '').toLowerCase();
                return cleanRut.includes(cleanSearch);
            });
            setFilteredLoans(filtered);
        } else {
            setFilteredLoans(allLoans);
        }
    };

    const handleSelectLoan = (loan) => {
        setSelectedLoan(loan);
        setSearchTerm('');
        setFilteredLoans(allLoans);
        setActiveStep(1);
        setError('');
    };

    // PASO 2: Crear multa
    const handleSubmit = async () => {
        if (!fineValue || fineValue <= 0) {
            setError('Por favor ingresa un valor válido para el cargo por reparación menor');
            return;
        }

        setLoading(true);
        setError('');

        try {
            await fineService.createFineForMinorDamage(selectedLoan.customer, selectedLoan.id, fineValue);

            alert('Multa creada exitosamente');
            navigate('/fines');
        } catch (err) {
            console.error('Error al crear multa:', err);

            let errorMessage = 'Error al guardar la multa';

            if (err.response?.data) {
                const data = err.response.data;
                if (typeof data === 'string') {
                    errorMessage = data;
                } else if (data.message) {
                    errorMessage = data.message;
                }
            }

            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    const goToPreviousStep = () => {
        setActiveStep(prev => Math.max(0, prev - 1));
        setError('');
    };

    if (loadingData) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate('/fines')}
                sx={{
                    mb: 3,
                    color: '#0A142E',
                    fontWeight: 500,
                    '&:hover': { backgroundColor: 'rgba(10, 20, 46, 0.05)' }
                }}
            >
                Volver a Multas
            </Button>

            <Card sx={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)' }}>
                <CardContent sx={{ p: 4 }}>
                    <Typography variant="h5" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 4 }}>
                        Crear Nueva Multa por Daño Menor
                    </Typography>

                    {/* Stepper */}
                    <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
                        <Step>
                            <StepLabel>Seleccionar Préstamo</StepLabel>
                        </Step>
                        <Step>
                            <StepLabel>Ingresar Valor del cargo por reparación menor</StepLabel>
                        </Step>
                    </Stepper>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    {/* Container con altura mínima consistente */}
                    <Box sx={{ minHeight: '400px', display: 'flex', flexDirection: 'column' }}>
                        {/* PASO 1: Seleccionar Préstamo */}
                        {activeStep === 0 && (
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
                                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 600, mb: 1 }}>
                                    Paso 1: Buscar Préstamo por RUT del Cliente
                                </Typography>

                                <TextField
                                    fullWidth
                                    label="Ingresa RUT del cliente"
                                    value={searchTerm}
                                    onChange={handleSearch}
                                    placeholder="Ej: 12.345.678-9"
                                    variant="outlined"
                                    size="medium"
                                />

                                {filteredLoans.length > 0 && searchTerm.trim() && (
                                    <Box sx={{ mt: 1 }}>
                                        <Typography variant="subtitle2" sx={{ mb: 2, color: '#666', fontWeight: 600 }}>
                                            Préstamos encontrados ({filteredLoans.length}):
                                        </Typography>
                                        <Paper elevation={0} sx={{ border: '1px solid #e0e0e0', borderRadius: 1, overflow: 'hidden', maxHeight: '400px', overflowY: 'auto' }}>
                                            <List sx={{ p: 0 }}>
                                                {filteredLoans.map((loan, idx) => (
                                                    <Box key={loan.id}>
                                                        <ListItemButton
                                                            onClick={() => handleSelectLoan(loan)}
                                                            sx={{
                                                                py: 1.5,
                                                                px: 2,
                                                                '&:hover': {
                                                                    backgroundColor: 'rgba(10, 20, 46, 0.04)',
                                                                },
                                                                transition: 'all 0.2s ease'
                                                            }}
                                                        >
                                                            <ListItemText
                                                                primary={`Préstamo #${loan.id} - ${loan.customer?.name || 'N/A'}`}
                                                                secondary={`RUT: ${formatRut(loan.customer?.rut) || 'N/A'} • Herramienta: ${loan.tool?.name || 'N/A'} • Estado: ${loan.status || 'N/A'}`}
                                                                primaryTypographyProps={{ variant: 'body2', fontWeight: 500, color: '#0A142E' }}
                                                                secondaryTypographyProps={{ variant: 'caption' }}
                                                            />
                                                        </ListItemButton>
                                                        {idx < filteredLoans.length - 1 && <Divider sx={{ m: 0 }} />}
                                                    </Box>
                                                ))}
                                            </List>
                                        </Paper>
                                    </Box>
                                )}

                                {searchTerm.trim() && filteredLoans.length === 0 && (
                                    <Alert severity="warning" sx={{ mt: 2 }}>
                                        No se encontraron préstamos con: "{searchTerm}"
                                    </Alert>
                                )}

                                {!searchTerm.trim() && (
                                    <Alert severity="info" sx={{ mt: 2 }}>
                                        Ingresa un RUT de cliente para buscar
                                    </Alert>
                                )}
                            </Box>
                        )}

                        {/* PASO 2: Ingresar Valor del cargo por daño menor */}
                        {activeStep === 1 && (
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
                                <Alert severity="info">
                                    <strong>Préstamo seleccionado:</strong>
                                    <br />
                                    ID Préstamo: <strong>#{selectedLoan.id}</strong>
                                    <br />
                                    Cliente: <strong>{selectedLoan.customer?.name || 'N/A'}</strong> ({formatRut(selectedLoan.customer?.rut) || 'N/A'})
                                    <br />
                                    Herramienta: <strong>{selectedLoan.tool?.name || 'N/A'}</strong> (ID: {selectedLoan.tool?.id || 'N/A'})
                                </Alert>

                                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 600, mb: 1 }}>
                                    Paso 2: Ingresar Valor del cargo por daño menor
                                </Typography>

                                <Box>
                                    <Typography variant="caption" sx={{ color: '#666', display: 'block', mb: 1, fontWeight: 500 }}>
                                        Valor de la Multa (en pesos chilenos)
                                    </Typography>
                                    <TextField
                                        fullWidth
                                        type="number"
                                        value={fineValue}
                                        onChange={(e) => setFineValue(e.target.value)}
                                        placeholder="Ej: 15000"
                                        InputLabelProps={{ shrink: true }}
                                        variant="outlined"
                                        size="medium"
                                        inputProps={{ min: 0, step: 100 }}
                                    />
                                </Box>

                                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 'auto', pt: 3 }}>
                                    <Button
                                        variant="outlined"
                                        onClick={goToPreviousStep}
                                        disabled={loading}
                                        sx={{ color: '#0A142E', borderColor: '#0A142E' }}
                                    >
                                        Atrás
                                    </Button>
                                    <Button
                                        variant="contained"
                                        onClick={handleSubmit}
                                        disabled={loading || !fineValue}
                                        sx={{
                                            backgroundColor: '#0A142E',
                                            color: '#FACC15',
                                            fontWeight: 600,
                                            '&:hover': { backgroundColor: '#1a2847' },
                                            '&:disabled': { backgroundColor: '#e0e0e0', color: '#9e9e9e' }
                                        }}
                                    >
                                        {loading ? <CircularProgress size={20} sx={{ color: '#FACC15', mr: 1 }} /> : null}
                                        {loading ? 'Creando...' : 'Crear Multa'}
                                    </Button>
                                </Box>
                            </Box>
                        )}
                    </Box>
                </CardContent>
            </Card>
        </Box>
    );
};

export default AddFineMinorDamage;