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
    Paper,
    List,
    ListItemButton,
    ListItemText,
    Divider,
    Radio,
    RadioGroup,
    FormControl,
    FormControlLabel
} from "@mui/material";
import { ArrowBack as ArrowBackIcon } from "@mui/icons-material";
import loanService from "../services/loan.service";

const LoanReturn = () => {
    const navigate = useNavigate();

    // Estados generales
    const [error, setError] = useState('');
    const [step, setStep] = useState(0); // 0: buscar préstamo, 1: seleccionar estado

    // Paso 1: Búsqueda de préstamos
    const [searchRut, setSearchRut] = useState('');
    const [searchId, setSearchId] = useState('');
    const [loans, setLoans] = useState([]);
    const [searchPerformed, setSearchPerformed] = useState(false);
    const [loadingLoans, setLoadingLoans] = useState(false);

    // Paso 2: Selección de préstamo y estado de herramienta
    const [selectedLoan, setSelectedLoan] = useState(null);
    const [toolCondition, setToolCondition] = useState('');
    const [submitting, setSubmitting] = useState(false);

    // Función para formatear RUT en tiempo real
    const formatRut = (value) => {
        const cleaned = value.replace(/[^0-9kK]/g, '');
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

        // Retornar con guion desde que hay al menos 2 caracteres
        return `${formattedBody}-${dv}`;
    };

    // Buscar préstamos por RUT - SOLO ACTIVOS
    const handleSearchByRut = async () => {
        if (!searchRut.trim()) {
            setError('Por favor ingresa un RUT');
            return;
        }

        setLoadingLoans(true);
        setError('');
        setLoans([]);
        setSearchPerformed(true);

        try {
            // Enviar el RUT tal como está formateado (con puntos y guion)
            const result = await loanService.findLoanByCustomerRut(searchRut.trim());
            const loanList = result.data || [];

            // Filtrar solo préstamos activos
            const activeLoans = loanList.filter(loan => loan.status === 'activo');
            setLoans(activeLoans);

            if (activeLoans.length === 0) {
                if (loanList.length > 0) {
                    setError(`No se encontraron préstamos activos para el RUT: "${searchRut}"`);
                } else {
                    setError(`No se encontraron préstamos para el RUT: "${searchRut}"`);
                }
            }
        } catch (err) {
            console.error('Error al buscar por RUT:', err);
            setError('Error al buscar préstamos. Intenta nuevamente.');
        } finally {
            setLoadingLoans(false);
        }
    };

    // Buscar préstamos por ID
    const handleSearchById = async () => {
        if (!searchId.trim()) {
            setError('Por favor ingresa un ID de préstamo');
            return;
        }

        setLoadingLoans(true);
        setError('');
        setLoans([]);
        setSearchPerformed(true);

        try {
            const result = await loanService.getLoanById(parseInt(searchId));
            const loanData = result.data;

            if (loanData) {
                setLoans([loanData]);
            } else {
                setError(`No se encontró préstamo con ID: "${searchId}"`);
            }
        } catch (err) {
            console.error('Error al buscar por ID:', err);
            setError(`No se encontró préstamo con ID: "${searchId}". Intenta nuevamente.`);
        } finally {
            setLoadingLoans(false);
        }
    };

    const handleSelectLoan = (loan) => {
        setSelectedLoan(loan);
        setStep(1);
        setError('');
    };

    // Registrar devolución
    const handleSubmitReturn = async () => {
        if (!toolCondition) {
            setError('Por favor selecciona el estado de la herramienta');
            return;
        }

        setSubmitting(true);
        setError('');

        try {
            await loanService.returnLoan(selectedLoan.id, toolCondition);

            alert('Préstamo devuelto exitosamente');
            navigate('/loans');
        } catch (err) {
            console.error('Error al devolver préstamo:', err);

            let errorMessage = 'Error al registrar la devolución';

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
            setSubmitting(false);
        }
    };

    const goBack = () => {
        if (step === 1) {
            setStep(0);
            setSelectedLoan(null);
            setToolCondition('');
            setError('');
        } else {
            navigate('/loans');
        }
    };

    const resetSearch = () => {
        setSearchRut('');
        setSearchId('');
        setLoans([]);
        setSearchPerformed(false);
        setError('');
    };

    return (
        <Box sx={{ p: 3 }}>
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate('/loans')}
                sx={{
                    mb: 3,
                    color: '#0A142E',
                    fontWeight: 500,
                    '&:hover': { backgroundColor: 'rgba(10, 20, 46, 0.05)' }
                }}
            >
                Volver a préstamos
            </Button>

            <Card sx={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)' }}>
                <CardContent sx={{ p: 4 }}>
                    <Typography variant="h5" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 3 }}>
                        Devolver Préstamo
                    </Typography>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    <Box sx={{ minHeight: '500px', display: 'flex', flexDirection: 'column' }}>
                        {/* PASO 1: Buscar Préstamo */}
                        {step === 0 && (
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 600 }}>
                                    Paso 1: Buscar Préstamo
                                </Typography>

                                {/* Búsqueda por RUT */}
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                                    <Typography variant="subtitle2" sx={{ color: '#0A142E', fontWeight: 500 }}>
                                        Buscar por RUT del Cliente (Solo préstamos activos)
                                    </Typography>
                                    <Box sx={{ display: 'flex', gap: 2 }}>
                                        <TextField
                                            fullWidth
                                            label="Ingresa RUT del cliente"
                                            value={searchRut}
                                            onChange={(e) => setSearchRut(formatRut(e.target.value))}
                                            placeholder="Ej: 12.345.678-9"
                                            variant="outlined"
                                            disabled={searchPerformed && searchRut}
                                        />
                                        <Button
                                            variant="contained"
                                            onClick={handleSearchByRut}
                                            disabled={loadingLoans || !searchRut.trim()}
                                            sx={{
                                                backgroundColor: '#0A142E',
                                                color: '#FACC15',
                                                fontWeight: 600,
                                                '&:hover': { backgroundColor: '#1a2847' },
                                                '&:disabled': { backgroundColor: '#e0e0e0', color: '#9e9e9e' }
                                            }}
                                        >
                                            {loadingLoans ? <CircularProgress size={20} /> : 'Buscar'}
                                        </Button>
                                    </Box>
                                </Box>

                                {/* Búsqueda por ID */}
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                                    <Typography variant="subtitle2" sx={{ color: '#0A142E', fontWeight: 500 }}>
                                        O buscar por ID del Préstamo
                                    </Typography>
                                    <Box sx={{ display: 'flex', gap: 2 }}>
                                        <TextField
                                            fullWidth
                                            label="Ingresa ID del préstamo"
                                            value={searchId}
                                            onChange={(e) => setSearchId(e.target.value)}
                                            placeholder="Ej: 123"
                                            variant="outlined"
                                            type="number"
                                            disabled={searchPerformed && searchId}
                                        />
                                        <Button
                                            variant="contained"
                                            onClick={handleSearchById}
                                            disabled={loadingLoans || !searchId.trim()}
                                            sx={{
                                                backgroundColor: '#0A142E',
                                                color: '#FACC15',
                                                fontWeight: 600,
                                                '&:hover': { backgroundColor: '#1a2847' },
                                                '&:disabled': { backgroundColor: '#e0e0e0', color: '#9e9e9e' }
                                            }}
                                        >
                                            {loadingLoans ? <CircularProgress size={20} /> : 'Buscar'}
                                        </Button>
                                    </Box>
                                </Box>

                                {/* Lista de préstamos */}
                                {loans.length > 0 && (
                                    <Box sx={{ mt: 2 }}>
                                        <Typography variant="subtitle2" sx={{ mb: 2, color: '#666', fontWeight: 600 }}>
                                            Préstamos encontrados ({loans.length}):
                                        </Typography>
                                        <Paper elevation={0} sx={{ border: '1px solid #e0e0e0', borderRadius: 1, overflow: 'hidden', maxHeight: '350px', overflowY: 'auto' }}>
                                            <List sx={{ p: 0 }}>
                                                {loans.map((loan, idx) => (
                                                    <Box key={loan.id}>
                                                        <ListItemButton
                                                            onClick={() => handleSelectLoan(loan)}
                                                            sx={{
                                                                py: 2,
                                                                px: 2,
                                                                '&:hover': {
                                                                    backgroundColor: 'rgba(10, 20, 46, 0.04)',
                                                                },
                                                                transition: 'all 0.2s ease'
                                                            }}
                                                        >
                                                            <ListItemText
                                                                primary={`ID: ${loan.id} • ${loan.customer?.name || 'Cliente'} - ${loan.tool?.name || 'Herramienta'}`}
                                                                secondary={`RUT: ${formatRut(loan.customer?.rut) || 'N/A'} • Fecha devolución: ${loan.returnDate || 'N/A'} • Estado: ${loan.status || 'N/A'}`}
                                                                primaryTypographyProps={{ variant: 'body2', fontWeight: 500, color: '#0A142E' }}
                                                                secondaryTypographyProps={{ variant: 'caption' }}
                                                            />
                                                        </ListItemButton>
                                                        {idx < loans.length - 1 && <Divider sx={{ m: 0 }} />}
                                                    </Box>
                                                ))}
                                            </List>
                                        </Paper>
                                    </Box>
                                )}

                                {searchPerformed && loans.length === 0 && !loadingLoans && (
                                    <Alert severity="info">
                                        No se encontraron préstamos con los criterios especificados
                                    </Alert>
                                )}

                                {searchPerformed && (
                                    <Button
                                        variant="outlined"
                                        onClick={resetSearch}
                                        sx={{ color: '#0A142E', borderColor: '#0A142E', mt: 2 }}
                                    >
                                        Nueva búsqueda
                                    </Button>
                                )}
                            </Box>
                        )}

                        {/* PASO 2: Seleccionar Estado de Herramienta */}
                        {step === 1 && selectedLoan && (
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                                <Alert severity="info">
                                    Cliente: <strong>{selectedLoan.customer?.name}</strong> ({formatRut(selectedLoan.customer?.rut)})
                                    <br />
                                    Herramienta: <strong>{selectedLoan.tool?.name}</strong>
                                    <br />
                                    Fecha de devolución: <strong>{selectedLoan.returnDate}</strong>
                                </Alert>

                                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 600 }}>
                                    Paso 2: Estado de la Herramienta
                                </Typography>

                                <FormControl component="fieldset">
                                    <Typography variant="subtitle2" sx={{ mb: 2, color: '#666', fontWeight: 600 }}>
                                        Estado de Devolución de la Herramienta *
                                    </Typography>
                                    <RadioGroup
                                        value={toolCondition}
                                        onChange={(e) => setToolCondition(e.target.value)}
                                    >
                                        <FormControlLabel
                                            value="buen estado"
                                            control={<Radio />}
                                            label="Buen Estado"
                                            sx={{
                                                p: 1.5,
                                                border: '1px solid #e0e0e0',
                                                borderRadius: 1,
                                                mb: 1,
                                                '&:hover': { backgroundColor: 'rgba(10, 20, 46, 0.02)' }
                                            }}
                                        />
                                        <FormControlLabel
                                            value="dañada"
                                            control={<Radio />}
                                            label="Dañada"
                                            sx={{
                                                p: 1.5,
                                                border: '1px solid #e0e0e0',
                                                borderRadius: 1,
                                                '&:hover': { backgroundColor: 'rgba(10, 20, 46, 0.02)' }
                                            }}
                                        />
                                    </RadioGroup>
                                </FormControl>

                                <Alert severity="warning">
                                    Asegúrate de seleccionar el estado correcto de la herramienta antes de confirmar
                                </Alert>

                                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 'auto', pt: 3 }}>
                                    <Button
                                        variant="outlined"
                                        onClick={goBack}
                                        disabled={submitting}
                                        sx={{ color: '#0A142E', borderColor: '#0A142E' }}
                                    >
                                        Atrás
                                    </Button>
                                    <Button
                                        variant="contained"
                                        onClick={handleSubmitReturn}
                                        disabled={submitting || !toolCondition}
                                        sx={{
                                            backgroundColor: '#0A142E',
                                            color: '#FACC15',
                                            fontWeight: 600,
                                            '&:hover': { backgroundColor: '#1a2847' },
                                            '&:disabled': { backgroundColor: '#e0e0e0', color: '#9e9e9e' }
                                        }}
                                    >
                                        {submitting ? <CircularProgress size={20} sx={{ color: '#FACC15', mr: 1 }} /> : null}
                                        {submitting ? 'Procesando...' : 'Confirmar Devolución'}
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

export default LoanReturn;