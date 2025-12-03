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
    Chip
} from "@mui/material";
import { ArrowBack as ArrowBackIcon } from "@mui/icons-material";
import customerService from "../services/customer.service";

const UpdateCustomer = () => {
    const navigate = useNavigate();

    const [searchMode, setSearchMode] = useState(true);
    const [searchValue, setSearchValue] = useState('');
    const [searchType, setSearchType] = useState('rut'); // 'rut' o 'id'

    const [customer, setCustomer] = useState(null);
    const [editFormData, setEditFormData] = useState({
        name: '',
        email: '',
        phoneNumber: ''
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

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

    const handleSearch = async () => {
        if (!searchValue.trim()) {
            setError('Por favor ingresa un valor para buscar');
            return;
        }

        setLoading(true);
        setError('');

        try {
            let response;
            if (searchType === 'rut') {
                // Enviar el RUT tal como está formateado (con puntos y guion)
                response = await customerService.getByRut(searchValue.trim());
            } else {
                response = await customerService.getById(searchValue.trim());
            }

            setCustomer(response.data);
            setEditFormData({
                name: response.data.name,
                email: response.data.email,
                phoneNumber: response.data.phoneNumber
            });
            setSearchMode(false);
            setError('');
        } catch (err) {
            console.error('Error al buscar cliente:', err);
            let errorMessage = 'Error al buscar el cliente';

            if (err.response?.status === 404) {
                errorMessage = `No se encontró cliente con ${searchType === 'rut' ? 'RUT' : 'ID'}: ${searchValue}`;
            } else if (err.response?.data) {
                errorMessage = err.response.data;
            }

            setError(errorMessage);
            setCustomer(null);
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
        if (!editFormData.name || !editFormData.email || !editFormData.phoneNumber) {
            setError('Por favor completa todos los campos');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const updatedCustomer = {
                id: customer.id,
                name: editFormData.name,
                rut: customer.rut,
                email: editFormData.email,
                phoneNumber: editFormData.phoneNumber,
                status: customer.status
            };

            await customerService.update(updatedCustomer);

            alert('Cliente actualizado exitosamente');
            navigate('/customers');
        } catch (err) {
            console.error('Error al actualizar:', err);

            let errorMessage = 'Error al actualizar el cliente';

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
        setCustomer(null);
        setSearchValue('');
        setError('');
    };

    const handleSearchValueChange = (e) => {
        const value = e.target.value;

        // Si el tipo de búsqueda es RUT, formatear
        if (searchType === 'rut') {
            setSearchValue(formatRut(value));
        } else {
            setSearchValue(value);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate('/customers')}
                sx={{
                    mb: 3,
                    color: '#0A142E',
                    '&:hover': {
                        backgroundColor: 'rgba(10, 20, 46, 0.05)'
                    }
                }}
            >
                Volver a clientes
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
                                Buscar Cliente para Actualizar
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
                                        label={searchType === 'rut' ? 'Ingresa RUT' : 'Ingresa ID'}
                                        value={searchValue}
                                        onChange={handleSearchValueChange}
                                        onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                        placeholder={searchType === 'rut' ? 'Ej: 12.345.678-9' : 'Ej: 1'}
                                    />
                                    <Button
                                        variant="outlined"
                                        onClick={() => {
                                            setSearchType(searchType === 'rut' ? 'id' : 'rut');
                                            setSearchValue('');
                                        }}
                                        sx={{ minWidth: '120px' }}
                                    >
                                        {searchType === 'rut' ? 'Buscar por ID' : 'Buscar por RUT'}
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
                                Actualizar Cliente
                            </Typography>

                            {error && (
                                <Alert severity="error" sx={{ mb: 3 }}>
                                    {error}
                                </Alert>
                            )}

                            {/* Datos del cliente (solo lectura) */}
                            <Box sx={{ mb: 4, p: 2, backgroundColor: '#f5f5f5', borderRadius: 1 }}>
                                <Typography variant="h6" sx={{ mb: 2, color: '#0A142E' }}>
                                    Información del Cliente
                                </Typography>

                                <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                                    <Box>
                                        <Typography variant="body2" sx={{ color: '#666', mb: 0.5 }}>
                                            <strong>ID:</strong>
                                        </Typography>
                                        <Typography variant="body1">{customer.id}</Typography>
                                    </Box>

                                    <Box>
                                        <Typography variant="body2" sx={{ color: '#666', mb: 0.5 }}>
                                            <strong>RUT:</strong>
                                        </Typography>
                                        <Typography variant="body1">{customer.rut}</Typography>
                                    </Box>

                                    <Box>
                                        <Typography variant="body2" sx={{ color: '#666', mb: 0.5 }}>
                                            <strong>Estado:</strong>
                                        </Typography>
                                        <Chip
                                            label={customer.status}
                                            color={customer.status === 'activo' ? 'success' : 'warning'}
                                            size="small"
                                        />
                                    </Box>
                                </Box>
                            </Box>

                            <Divider sx={{ my: 3 }} />

                            {/* Formulario de edición */}
                            <Typography variant="h6" sx={{ mb: 2, color: '#0A142E' }}>
                                Editar Datos
                            </Typography>

                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                                <TextField
                                    fullWidth
                                    label="Nombre"
                                    name="name"
                                    value={editFormData.name}
                                    onChange={handleEditChange}
                                    required
                                />

                                <TextField
                                    fullWidth
                                    type="email"
                                    label="Email"
                                    name="email"
                                    value={editFormData.email}
                                    onChange={handleEditChange}
                                    required
                                />

                                <TextField
                                    fullWidth
                                    label="Teléfono"
                                    name="phoneNumber"
                                    value={editFormData.phoneNumber}
                                    onChange={handleEditChange}
                                    required
                                />

                                <Alert severity="info">
                                    No se pueden modificar: ID, RUT ni Estado
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
                                        {loading ? <CircularProgress size={24} sx={{ color: '#FACC15' }} /> : 'Actualizar Cliente'}
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

export default UpdateCustomer;