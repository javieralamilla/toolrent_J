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
    Paper,
    List,
    ListItemButton,
    ListItemText,
    Divider,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Chip
} from "@mui/material";
import {
    ArrowBack as ArrowBackIcon,
    CheckCircle as CheckCircleIcon,
    Payment as PaymentIcon
} from "@mui/icons-material";
import fineService from "../services/fine.service";

const PayFine = () => {
    const navigate = useNavigate();

    // Datos generales
    const [allFines, setAllFines] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(true);
    const [error, setError] = useState('');

    // Buscar multas
    const [searchTerm, setSearchTerm] = useState('');
    const [filteredFines, setFilteredFines] = useState([]);
    const [selectedFine, setSelectedFine] = useState(null);

    // Dialog de confirmación
    const [openDialog, setOpenDialog] = useState(false);

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

    // Normalizar RUT para comparación
    const normalizeRut = (rut) => {
        return rut?.replace(/\./g, '').replace(/-/g, '').toLowerCase() || '';
    };

    // Cargar datos iniciales - Solo multas NO PAGADAS
    useEffect(() => {
        const fetchData = async () => {
            setLoadingData(true);
            try {
                const finesRes = await fineService.getByStatus('no pagada');

                setAllFines(finesRes.data || []);
                setFilteredFines(finesRes.data || []);
                setError('');
            } catch (err) {
                console.error('Error al cargar datos:', err);
                setError('Error al cargar multas pendientes de pago');
            } finally {
                setLoadingData(false);
            }
        };

        fetchData();
    }, []);

    // Buscar multas por RUT del cliente
    const handleSearch = (e) => {
        let value = e.target.value;

        // Formatear automáticamente mientras se escribe
        value = formatRut(value);
        setSearchTerm(value);

        if (value.trim()) {
            const normalizedSearch = normalizeRut(value);

            const filtered = allFines.filter(fine => {
                const normalizedRut = normalizeRut(fine.customer?.rut);
                return normalizedRut.includes(normalizedSearch);
            });
            setFilteredFines(filtered);
        } else {
            setFilteredFines(allFines);
        }
    };

    const handleSelectFine = (fine) => {
        setSelectedFine(fine);
        setOpenDialog(true);
        setError('');
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedFine(null);
    };

    // Pagar multa
    const handleSubmit = async () => {
        setLoading(true);
        setError('');

        try {
            await fineService.payFine(selectedFine);

            alert('Multa pagada exitosamente');
            navigate('/fines');
        } catch (err) {
            console.error('Error al pagar multa:', err);

            let errorMessage = 'Error al procesar el pago de la multa';

            if (err.response?.data) {
                const data = err.response.data;
                if (typeof data === 'string') {
                    errorMessage = data;
                } else if (data.message) {
                    errorMessage = data.message;
                }
            }

            setError(errorMessage);
            setOpenDialog(false);
        } finally {
            setLoading(false);
        }
    };

    // Función para formatear moneda
    const formatCurrency = (value) => {
        return new Intl.NumberFormat('es-CL', {
            style: 'currency',
            currency: 'CLP'
        }).format(value);
    };

    // Función para obtener el color según el tipo de multa
    const getTypeColor = (type) => {
        const colors = {
            'atraso': '#ff9800',
            'daño leve': '#f57c00',
            'daño irreparable': '#d32f2f'
        };
        return colors[type?.toLowerCase()] || '#757575';
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
                    <Typography variant="h5" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 2 }}>
                        Pagar Multa
                    </Typography>

                    <Alert severity="info" icon={<PaymentIcon />} sx={{ mb: 4 }}>
                        <strong>Busca las multas pendientes de pago</strong> por RUT del cliente
                    </Alert>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    {/* Container con altura FIJA consistente */}
                    <Box sx={{ height: '500px', display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5, height: '100%', overflow: 'auto' }}>
                            <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 600, mb: 1 }}>
                                Buscar Multas Pendientes por RUT del Cliente
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

                            {filteredFines.length > 0 && searchTerm.trim() && (
                                <Box sx={{ mt: 1, flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
                                    <Typography variant="subtitle2" sx={{ mb: 2, color: '#666', fontWeight: 600, flexShrink: 0 }}>
                                        Multas pendientes encontradas ({filteredFines.length}):
                                    </Typography>
                                    <Paper elevation={0} sx={{
                                        border: '1px solid #e0e0e0',
                                        borderRadius: 1,
                                        overflow: 'hidden',
                                        flex: 1,
                                        display: 'flex',
                                        flexDirection: 'column'
                                    }}>
                                        <List sx={{ p: 0, overflow: 'auto', flex: 1 }}>
                                            {filteredFines.map((fine, idx) => (
                                                <Box key={fine.id}>
                                                    <ListItemButton
                                                        onClick={() => handleSelectFine(fine)}
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
                                                            primary={
                                                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexWrap: 'wrap' }}>
                                                                    <Typography variant="body2" fontWeight="bold">
                                                                        Multa #{fine.id}
                                                                    </Typography>
                                                                    <Chip
                                                                        label={fine.type}
                                                                        size="small"
                                                                        sx={{
                                                                            backgroundColor: getTypeColor(fine.type),
                                                                            color: 'white',
                                                                            fontWeight: 600,
                                                                            textTransform: 'capitalize'
                                                                        }}
                                                                    />
                                                                    <Typography variant="body2" fontWeight="bold" color="error">
                                                                        {formatCurrency(fine.fineValue)}
                                                                    </Typography>
                                                                </Box>
                                                            }
                                                            secondary={
                                                                <>
                                                                    Cliente: {fine.customer?.name || 'N/A'} (RUT: {fine.customer?.rut || 'N/A'})
                                                                    <br />
                                                                    Préstamo: #{fine.loan?.id || 'N/A'} • Herramienta: {fine.loan?.tool?.name || 'N/A'}
                                                                </>
                                                            }
                                                            primaryTypographyProps={{ component: 'div' }}
                                                            secondaryTypographyProps={{ variant: 'caption' }}
                                                        />
                                                    </ListItemButton>
                                                    {idx < filteredFines.length - 1 && <Divider sx={{ m: 0 }} />}
                                                </Box>
                                            ))}
                                        </List>
                                    </Paper>
                                </Box>
                            )}

                            {searchTerm.trim() && filteredFines.length === 0 && (
                                <Alert severity="warning" sx={{ mt: 2, flexShrink: 0 }}>
                                    No se encontraron multas pendientes con RUT: "{searchTerm}"
                                </Alert>
                            )}

                            {!searchTerm.trim() && allFines.length > 0 && (
                                <Alert severity="info" sx={{ mt: 2, flexShrink: 0 }}>
                                    Ingresa un RUT de cliente para buscar multas pendientes de pago
                                </Alert>
                            )}

                            {!searchTerm.trim() && allFines.length === 0 && (
                                <Alert severity="success" sx={{ mt: 2, flexShrink: 0 }}>
                                    ¡No hay multas pendientes de pago! Todas las multas están pagadas.
                                </Alert>
                            )}
                        </Box>
                    </Box>
                </CardContent>
            </Card>

            {/* Dialog de confirmación */}
            <Dialog
                open={openDialog}
                onClose={handleCloseDialog}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle sx={{ backgroundColor: '#4caf50', color: 'white', display: 'flex', alignItems: 'center', gap: 1 }}>
                    <PaymentIcon />
                    Confirmar Pago de Multa
                </DialogTitle>
                <DialogContent sx={{ mt: 2 }}>
                    {selectedFine && (
                        <Box>
                            <Alert severity="info" sx={{ mb: 2 }}>
                                Estás a punto de registrar el pago de esta multa.
                            </Alert>

                            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
                                Detalles de la Multa:
                            </Typography>

                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pl: 2 }}>
                                <Box>
                                    <Typography variant="caption" color="textSecondary">ID Multa:</Typography>
                                    <Typography variant="body2" fontWeight="medium">#{selectedFine.id}</Typography>
                                </Box>

                                <Box>
                                    <Typography variant="caption" color="textSecondary">Tipo de Multa:</Typography>
                                    <Chip
                                        label={selectedFine.type}
                                        size="small"
                                        sx={{
                                            backgroundColor: getTypeColor(selectedFine.type),
                                            color: 'white',
                                            fontWeight: 600,
                                            textTransform: 'capitalize',
                                            mt: 0.5
                                        }}
                                    />
                                </Box>

                                <Box>
                                    <Typography variant="caption" color="textSecondary">Monto a Pagar:</Typography>
                                    <Typography variant="h6" fontWeight="bold" color="error">
                                        {formatCurrency(selectedFine.fineValue)}
                                    </Typography>
                                </Box>

                                <Divider />

                                <Box>
                                    <Typography variant="caption" color="textSecondary">Cliente:</Typography>
                                    <Typography variant="body2" fontWeight="medium">
                                        {selectedFine.customer?.name || 'N/A'}
                                    </Typography>
                                    <Typography variant="caption" color="textSecondary">
                                        RUT: {selectedFine.customer?.rut || 'N/A'}
                                    </Typography>
                                </Box>

                                <Box>
                                    <Typography variant="caption" color="textSecondary">Préstamo Asociado:</Typography>
                                    <Typography variant="body2" fontWeight="medium">
                                        Préstamo #{selectedFine.loan?.id || 'N/A'}
                                    </Typography>
                                    <Typography variant="caption" color="textSecondary">
                                        Herramienta: {selectedFine.loan?.tool?.name || 'N/A'}
                                    </Typography>
                                </Box>
                            </Box>

                            <Alert severity="success" sx={{ mt: 3 }} icon={<CheckCircleIcon />}>
                                <strong>¿Confirmas el pago de esta multa?</strong>
                                <br />
                                Esta acción marcará la multa como pagada y actualizará el estado del cliente.
                            </Alert>
                        </Box>
                    )}
                </DialogContent>
                <DialogActions sx={{ p: 2, gap: 1 }}>
                    <Button
                        onClick={handleCloseDialog}
                        disabled={loading}
                        sx={{ color: '#666' }}
                    >
                        Cancelar
                    </Button>
                    <Button
                        onClick={handleSubmit}
                        disabled={loading}
                        variant="contained"
                        startIcon={loading ? null : <CheckCircleIcon />}
                        sx={{
                            backgroundColor: '#4caf50',
                            color: 'white',
                            fontWeight: 600,
                            '&:hover': { backgroundColor: '#45a049' },
                            '&:disabled': { backgroundColor: '#e0e0e0', color: '#9e9e9e' }
                        }}
                    >
                        {loading ? <CircularProgress size={20} sx={{ color: 'white', mr: 1 }} /> : null}
                        {loading ? 'Procesando...' : 'Sí, Confirmar Pago'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default PayFine;