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
    Divider,
    Chip,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions
} from "@mui/material";
import { ArrowBack as ArrowBackIcon } from "@mui/icons-material";
import toolService from "../services/tool.service";

const UpdateReplacementValue = () => {
    const navigate = useNavigate();

    const [searchMode, setSearchMode] = useState(true);
    const [searchValue, setSearchValue] = useState('');

    const [toolsInventory, setToolsInventory] = useState(null);
    const [editFormData, setEditFormData] = useState({
        replacementValue: ''
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);

    const handleSearch = async () => {
        if (!searchValue.trim()) {
            setError('Por favor ingresa un valor para buscar');
            return;
        }

        const searchNum = parseInt(searchValue.trim());
        if (isNaN(searchNum) || searchNum <= 0) {
            setError('El ID debe ser un número mayor a 0');
            return;
        }

        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const response = await toolService.findInventoryById(searchValue.trim());

            if (!response.data) {
                setError('No se encontraron datos para esta herramienta');
                setToolsInventory(null);
                return;
            }

            setToolsInventory(response.data);
            setEditFormData({
                replacementValue: response.data.replacementValue || ''
            });
            setSearchMode(false);
        } catch (err) {
            console.error('Error al buscar el inventario de la herramienta:', err);
            let errorMessage = 'Error al buscar el inventario de la herramienta';

            if (err.response?.status === 404) {
                errorMessage = `No se encontró el inventario de la herramienta con ID: ${searchValue}`;
            } else if (err.response?.data) {
                errorMessage = err.response.data;
            }

            setError(errorMessage);
            setToolsInventory(null);
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
        setError('');
    };

    const validateAndUpdate = () => {
        if (!editFormData.replacementValue) {
            setError('Por favor completa el campo de valor de reposición');
            return;
        }

        const replacementValue = parseInt(editFormData.replacementValue);

        if (isNaN(replacementValue)) {
            setError('El valor de reposición debe ser un número válido');
            return;
        }

        if (replacementValue <= 0) {
            setError('El valor de reposición debe ser mayor a 0');
            return;
        }

        setOpenConfirmDialog(true);
    };

    const handleUpdate = async () => {
        setOpenConfirmDialog(false);
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const replacementValue = parseInt(editFormData.replacementValue);

            await toolService.updateReplacementValue(toolsInventory.id, replacementValue);

            setSuccess('Valor de reposición de la herramienta actualizado exitosamente');

            setTimeout(() => {
                navigate('/toolsInventory');
            }, 1500);
        } catch (err) {
            console.error('Error al actualizar:', err);

            let errorMessage = 'Error al actualizar el valor de reposición de la herramienta';

            if (err.response?.data) {
                errorMessage = err.response.data;
            }

            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    const handleClear = () => {
        setSearchValue('');
        setError('');
        setSuccess('');
    };

    const handleBack = () => {
        setSearchMode(true);
        setToolsInventory(null);
        setSearchValue('');
        setEditFormData({ replacementValue: '' });
        setError('');
        setSuccess('');
    };

    return (
        <Box sx={{ p: 3 }}>
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate('/toolsInventory')}
                aria-label="Volver a inventario de herramientas"
                sx={{
                    mb: 3,
                    color: '#0A142E',
                    '&:hover': {
                        backgroundColor: 'rgba(10, 20, 46, 0.05)'
                    }
                }}
            >
                Volver a inventario de herramientas
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
                                Buscar herramienta para Actualizar
                            </Typography>

                            {error && (
                                <Alert severity="error" sx={{ mb: 3 }} role="alert">
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
                                        inputProps={{
                                            min: 1,
                                            'aria-label': 'ID de la herramienta'
                                        }}
                                    />
                                    <Button
                                        variant="outlined"
                                        onClick={handleClear}
                                        aria-label="Limpiar campo de búsqueda"
                                        sx={{ minWidth: '120px' }}
                                    >
                                        Limpiar
                                    </Button>
                                </Box>

                                <Button
                                    variant="contained"
                                    onClick={handleSearch}
                                    disabled={!searchValue.trim() || loading}
                                    aria-label="Buscar herramienta"
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
                                Actualizar el valor de reposición de la Herramienta
                            </Typography>

                            {error && (
                                <Alert severity="error" sx={{ mb: 3 }} role="alert">
                                    {error}
                                </Alert>
                            )}

                            {success && (
                                <Alert severity="success" sx={{ mb: 3 }} role="status">
                                    {success}
                                </Alert>
                            )}

                            {/* Datos del inventario de la herramienta (solo lectura) */}
                            <Box sx={{ mb: 4, p: 2, backgroundColor: '#f5f5f5', borderRadius: 1 }}>
                                <Typography variant="h6" sx={{ mb: 2, color: '#0A142E' }}>
                                    Información del Inventario
                                </Typography>

                                <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                                    <Box>
                                        <Typography variant="body2" sx={{ color: '#666', mb: 0.5 }}>
                                            <strong>ID:</strong>
                                        </Typography>
                                        <Typography variant="body1">{toolsInventory.id}</Typography>
                                    </Box>

                                    <Box>
                                        <Typography variant="body2" sx={{ color: '#666', mb: 0.5 }}>
                                            <strong>Nombre:</strong>
                                        </Typography>
                                        <Typography variant="body1">{toolsInventory.name}</Typography>
                                    </Box>

                                    <Box>
                                        <Typography variant="body2" sx={{ color: '#666', mb: 0.5 }}>
                                            <strong>Categoría:</strong>
                                        </Typography>
                                        <Chip
                                            label={toolsInventory.category}
                                            size="small"
                                        />
                                    </Box>
                                </Box>
                            </Box>

                            <Divider sx={{ my: 3 }} />

                            {/* Formulario de edición */}
                            <Typography variant="h6" sx={{ mb: 2, color: '#0A142E' }}>
                                Editar Valor de Reposición
                            </Typography>

                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    label="Valor de Reposición"
                                    name="replacementValue"
                                    value={editFormData.replacementValue}
                                    onChange={handleEditChange}
                                    required
                                    aria-label="Valor de reposición"
                                    inputProps={{ min: 1, step: "1" }}
                                />

                                <Alert severity="info">
                                    No se pueden modificar: ID, nombre, categoría, cantidad total, stock actual.
                                </Alert>

                                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                                    <Button
                                        variant="outlined"
                                        onClick={handleBack}
                                        disabled={loading}
                                        aria-label="Cancelar actualización"
                                    >
                                        Cancelar
                                    </Button>
                                    <Button
                                        variant="contained"
                                        onClick={validateAndUpdate}
                                        disabled={loading}
                                        aria-label="Actualizar herramienta"
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
                                        {loading ? <CircularProgress size={24} sx={{ color: '#FACC15' }} /> : 'Actualizar Herramienta'}
                                    </Button>
                                </Box>
                            </Box>
                        </>
                    )}
                </CardContent>
            </Card>

            {/* Diálogo de confirmación */}
            <Dialog
                open={openConfirmDialog}
                onClose={() => setOpenConfirmDialog(false)}
                aria-labelledby="confirm-dialog-title"
            >
                <DialogTitle id="confirm-dialog-title">
                    Confirmar Actualización
                </DialogTitle>
                <DialogContent>
                    <Typography sx={{ mt: 2 }}>
                        ¿Está seguro de que desea actualizar el valor de reposición de la herramienta <strong>{toolsInventory?.name}</strong> a <strong>${editFormData.replacementValue}</strong>?
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button
                        onClick={() => setOpenConfirmDialog(false)}
                        disabled={loading}
                    >
                        Cancelar
                    </Button>
                    <Button
                        onClick={handleUpdate}
                        disabled={loading}
                        variant="contained"
                        sx={{
                            backgroundColor: '#0A142E',
                            color: '#FACC15',
                            '&:hover': {
                                backgroundColor: '#1a2847'
                            }
                        }}
                    >
                        {loading ? <CircularProgress size={20} sx={{ color: '#FACC15' }} /> : 'Confirmar'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default UpdateReplacementValue;