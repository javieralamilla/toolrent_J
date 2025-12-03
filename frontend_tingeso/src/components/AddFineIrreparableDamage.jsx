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
    DialogActions
} from "@mui/material";
import { ArrowBack as ArrowBackIcon, Warning as WarningIcon } from "@mui/icons-material";
import fineService from "../services/fine.service";
import loanService from "../services/loan.service";

const AddFineIrreparableDamage = () => {
    const navigate = useNavigate();

    // Datos generales
    const [allLoans, setAllLoans] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(true);
    const [error, setError] = useState('');

    // Buscar préstamo
    const [searchTerm, setSearchTerm] = useState('');
    const [filteredLoans, setFilteredLoans] = useState([]);
    const [selectedLoan, setSelectedLoan] = useState(null);

    // Dialog de confirmación
    const [openDialog, setOpenDialog] = useState(false);

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

    // Buscar préstamo por RUT del cliente con formateo en tiempo real
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
        setOpenDialog(true);
        setError('');
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedLoan(null);
    };

    // Crear multa por daño irreparable
    const handleSubmit = async () => {
        setLoading(true);
        setError('');

        try {
            await fineService.createForIrreparableDamage(selectedLoan.customer, selectedLoan.id);

            alert('Multa por daño irreparable creada exitosamente');
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
            setOpenDialog(false);
        } finally {
            setLoading(false);
        }
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
                        Crear Nueva Multa por Daño Irreparable
                    </Typography>

                    <Alert severity="warning" icon={<WarningIcon />} sx={{ mb: 4 }}>
                        <strong>Importante:</strong> Esta multa se generará automáticamente con el valor de reemplazo de la herramienta.
                        La herramienta quedará marcada como dado de baja.
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
                                Buscar Préstamo por RUT del Cliente
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
                                <Box sx={{ mt: 1, flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
                                    <Typography variant="subtitle2" sx={{ mb: 2, color: '#666', fontWeight: 600, flexShrink: 0 }}>
                                        Préstamos encontrados ({filteredLoans.length}):
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
                                <Alert severity="warning" sx={{ mt: 2, flexShrink: 0 }}>
                                    No se encontraron préstamos con: "{searchTerm}"
                                </Alert>
                            )}

                            {!searchTerm.trim() && (
                                <Alert severity="info" sx={{ mt: 2, flexShrink: 0 }}>
                                    Ingresa un RUT de cliente para buscar
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
                <DialogTitle sx={{ backgroundColor: '#f44336', color: 'white', display: 'flex', alignItems: 'center', gap: 1 }}>
                    <WarningIcon />
                    Confirmar Multa por Daño Irreparable
                </DialogTitle>
                <DialogContent sx={{ mt: 2 }}>
                    {selectedLoan && (
                        <Box>
                            <Alert severity="warning" sx={{ mb: 2 }}>
                                Esta acción creará una multa por el valor de reemplazo de la herramienta y la dará de baja permanentemente.
                            </Alert>

                            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
                                Detalles del Préstamo:
                            </Typography>

                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5, pl: 2 }}>
                                <Box>
                                    <Typography variant="caption" color="textSecondary">ID Préstamo:</Typography>
                                    <Typography variant="body2" fontWeight="medium">#{selectedLoan.id}</Typography>
                                </Box>

                                <Box>
                                    <Typography variant="caption" color="textSecondary">Cliente:</Typography>
                                    <Typography variant="body2" fontWeight="medium">
                                        {selectedLoan.customer?.name || 'N/A'}
                                    </Typography>
                                    <Typography variant="caption" color="textSecondary">
                                        RUT: {formatRut(selectedLoan.customer?.rut) || 'N/A'}
                                    </Typography>
                                </Box>

                                <Box>
                                    <Typography variant="caption" color="textSecondary">Herramienta:</Typography>
                                    <Typography variant="body2" fontWeight="medium">
                                        {selectedLoan.tool?.name || 'N/A'}
                                    </Typography>
                                    <Typography variant="caption" color="textSecondary">
                                        ID: {selectedLoan.tool?.id || 'N/A'}
                                    </Typography>
                                </Box>

                                <Box>
                                    <Typography variant="caption" color="textSecondary">Estado del préstamo:</Typography>
                                    <Typography variant="body2" fontWeight="medium">
                                        {selectedLoan.status || 'N/A'}
                                    </Typography>
                                </Box>
                            </Box>

                            <Alert severity="error" sx={{ mt: 3 }}>
                                <strong>¿Estás seguro de que deseas crear esta multa?</strong>
                                <br />
                                La herramienta será dada de baja y no podrá ser prestada nuevamente.
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
                        sx={{
                            backgroundColor: '#f44336',
                            color: 'white',
                            fontWeight: 600,
                            '&:hover': { backgroundColor: '#d32f2f' },
                            '&:disabled': { backgroundColor: '#e0e0e0', color: '#9e9e9e' }
                        }}
                    >
                        {loading ? <CircularProgress size={20} sx={{ color: 'white', mr: 1 }} /> : null}
                        {loading ? 'Creando...' : 'Sí, Crear Multa'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default AddFineIrreparableDamage;