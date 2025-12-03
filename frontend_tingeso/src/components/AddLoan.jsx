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
import loanService from "../services/loan.service";
import toolService from "../services/tool.service";
import customerService from "../services/customer.service";

const AddLoan = () => {
    const navigate = useNavigate();

    // Estado del flujo
    const [activeStep, setActiveStep] = useState(0);

    // Datos generales
    const [allCustomers, setAllCustomers] = useState([]);
    const [allTools, setAllTools] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(true);
    const [error, setError] = useState('');

    // Paso 1: Seleccionar Cliente
    const [searchRut, setSearchRut] = useState('');
    const [filteredCustomers, setFilteredCustomers] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState(null);

    // Paso 2: Seleccionar Herramienta
    const [searchToolName, setSearchToolName] = useState('');
    const [filteredTools, setFilteredTools] = useState([]);
    const [selectedTool, setSelectedTool] = useState(null);

    // Paso 3: Fechas
    const [returnDate, setReturnDate] = useState('');

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

    // Normalizar RUT para comparación (sin puntos ni guiones)
    const normalizeRut = (rut) => {
        return rut?.replace(/\./g, '').replace(/-/g, '').toLowerCase() || '';
    };

    // Función para obtener fecha actual en formato YYYY-MM-DD
    const getTodayDate = () => {
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    // Cargar datos iniciales
    useEffect(() => {
        const fetchData = async () => {
            setLoadingData(true);
            try {
                const [customersRes, toolsRes] = await Promise.all([
                    customerService.getAll(),
                    toolService.getByStatus('disponible')
                ]);

                // Ordenar herramientas por ID de menor a mayor
                const sortedTools = (toolsRes.data || []).sort((a, b) => a.id - b.id);

                setAllCustomers(customersRes.data || []);
                setAllTools(sortedTools);
                setFilteredCustomers(customersRes.data || []);
                setFilteredTools(sortedTools);
                setError('');
            } catch (err) {
                console.error('Error al cargar datos:', err);
                setError('Error al cargar clientes o herramientas');
            } finally {
                setLoadingData(false);
            }
        };

        fetchData();
    }, []);

    // PASO 1: Buscar cliente por RUT (filtrado local)
    const handleSearchRut = (e) => {
        let value = e.target.value;

        // Formatear automáticamente mientras se escribe
        value = formatRut(value);
        setSearchRut(value);

        if (value.trim()) {
            // Normalizar tanto el valor de búsqueda como los RUTs de los clientes
            const normalizedSearch = normalizeRut(value);

            const filtered = allCustomers.filter(customer => {
                const normalizedCustomerRut = normalizeRut(customer.rut);
                return normalizedCustomerRut.includes(normalizedSearch);
            });
            setFilteredCustomers(filtered);
        } else {
            setFilteredCustomers(allCustomers);
        }
    };

    const handleSelectCustomer = (customer) => {
        setSelectedCustomer(customer);
        setSearchRut('');
        setFilteredCustomers(allCustomers);
        setActiveStep(1);
        setError('');
    };

    // PASO 2: Buscar herramienta por nombre (filtrado local con ordenamiento)
    const handleSearchToolName = (e) => {
        const value = e.target.value;
        setSearchToolName(value);

        if (value.trim()) {
            const filtered = allTools
                .filter(tool => tool.name.toLowerCase().includes(value.toLowerCase()))
                .sort((a, b) => a.id - b.id); // Ordenar resultados filtrados por ID
            setFilteredTools(filtered);
        } else {
            // Cuando no hay búsqueda, mostrar todos ordenados por ID
            setFilteredTools([...allTools].sort((a, b) => a.id - b.id));
        }
    };

    const handleSelectTool = (tool) => {
        setSelectedTool(tool);
        setSearchToolName('');
        setFilteredTools(allTools);
        setActiveStep(2);
        setError('');
    };

    // PASO 3: Crear préstamo
    const handleSubmit = async () => {
        if (!returnDate) {
            setError('Por favor ingresa la fecha de devolución');
            return;
        }

        const loanDate = getTodayDate();

        if (new Date(returnDate) <= new Date(loanDate)) {
            setError('La fecha de devolución debe ser posterior a hoy');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const loanData = {
                customer: selectedCustomer,
                tool: selectedTool,
                loanDate: loanDate,
                returnDate: returnDate,
                status: 'activo'
            };

            await loanService.createLoan(loanData);

            alert('Préstamo creado exitosamente');
            navigate('/loans');
        } catch (err) {
            console.error('Error al crear préstamo:', err);

            let errorMessage = 'Error al guardar el préstamo';

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
                    <Typography variant="h5" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 4 }}>
                        Crear Nuevo Préstamo
                    </Typography>

                    {/* Stepper */}
                    <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
                        <Step>
                            <StepLabel>Seleccionar Cliente</StepLabel>
                        </Step>
                        <Step>
                            <StepLabel>Seleccionar Herramienta</StepLabel>
                        </Step>
                        <Step>
                            <StepLabel>Ingresar Fechas</StepLabel>
                        </Step>
                    </Stepper>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    {/* Container con altura mínima consistente */}
                    <Box sx={{ minHeight: '500px', display: 'flex', flexDirection: 'column' }}>
                        {/* PASO 1: Seleccionar Cliente */}
                        {activeStep === 0 && (
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
                                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 600, mb: 1 }}>
                                    Paso 1: Buscar Cliente por RUT
                                </Typography>

                                <TextField
                                    fullWidth
                                    label="Ingresa RUT del cliente"
                                    value={searchRut}
                                    onChange={handleSearchRut}
                                    placeholder="Ej: 12.345.678-9"
                                    variant="outlined"
                                    size="medium"
                                />

                                {filteredCustomers.length > 0 && (
                                    <Box sx={{ mt: 1 }}>
                                        <Typography variant="subtitle2" sx={{ mb: 2, color: '#666', fontWeight: 600 }}>
                                            Clientes encontrados ({filteredCustomers.length}):
                                        </Typography>
                                        <Paper elevation={0} sx={{ border: '1px solid #e0e0e0', borderRadius: 1, overflow: 'hidden' }}>
                                            <List sx={{ p: 0 }}>
                                                {filteredCustomers.map((customer, idx) => (
                                                    <Box key={customer.id}>
                                                        <ListItemButton
                                                            onClick={() => handleSelectCustomer(customer)}
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
                                                                primary={`${customer.name}`}
                                                                secondary={`${formatRut(customer.rut)} • ${customer.email}`}
                                                                primaryTypographyProps={{ variant: 'body2', fontWeight: 500, color: '#0A142E' }}
                                                                secondaryTypographyProps={{ variant: 'caption' }}
                                                            />
                                                        </ListItemButton>
                                                        {idx < filteredCustomers.length - 1 && <Divider sx={{ m: 0 }} />}
                                                    </Box>
                                                ))}
                                            </List>
                                        </Paper>
                                    </Box>
                                )}

                                {searchRut.trim() && filteredCustomers.length === 0 && (
                                    <Alert severity="warning" sx={{ mt: 2 }}>
                                        No se encontraron clientes con RUT: "{searchRut}"
                                    </Alert>
                                )}

                                {!searchRut && filteredCustomers.length === 0 && (
                                    <Alert severity="info" sx={{ mt: 2 }}>
                                        Ingresa un RUT para buscar clientes
                                    </Alert>
                                )}
                            </Box>
                        )}

                        {/* PASO 2: Seleccionar Herramienta */}
                        {activeStep === 1 && (
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
                                <Alert severity="info" sx={{ mb: 1 }}>
                                    Cliente seleccionado: <strong>{selectedCustomer.name}</strong> ({formatRut(selectedCustomer.rut)})
                                </Alert>

                                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 600, mb: 1 }}>
                                    Paso 2: Buscar Herramienta por Nombre
                                </Typography>

                                <TextField
                                    fullWidth
                                    label="Ingresa nombre de la herramienta"
                                    value={searchToolName}
                                    onChange={handleSearchToolName}
                                    placeholder="Ej: taladro"
                                    variant="outlined"
                                    size="medium"
                                />

                                {filteredTools.length > 0 && (
                                    <Box sx={{ mt: 1 }}>
                                        <Typography variant="subtitle2" sx={{ mb: 2, color: '#666', fontWeight: 600 }}>
                                            Herramientas encontradas ({filteredTools.length}):
                                        </Typography>
                                        <Paper elevation={0} sx={{ border: '1px solid #e0e0e0', borderRadius: 1, overflow: 'hidden', maxHeight: '300px', overflowY: 'auto' }}>
                                            <List sx={{ p: 0 }}>
                                                {filteredTools.map((tool, idx) => (
                                                    <Box key={tool.id}>
                                                        <ListItemButton
                                                            onClick={() => handleSelectTool(tool)}
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
                                                                primary={`${tool.name}`}
                                                                secondary={`ID: ${tool.id} • Categoría: ${tool.category?.name || 'Sin categoría'} • Estado: ${tool.status}`}
                                                                primaryTypographyProps={{ variant: 'body2', fontWeight: 500, color: '#0A142E' }}
                                                                secondaryTypographyProps={{ variant: 'caption' }}
                                                            />
                                                        </ListItemButton>
                                                        {idx < filteredTools.length - 1 && <Divider sx={{ m: 0 }} />}
                                                    </Box>
                                                ))}
                                            </List>
                                        </Paper>
                                    </Box>
                                )}

                                {searchToolName.trim() && filteredTools.length === 0 && (
                                    <Alert severity="warning" sx={{ mt: 2 }}>
                                        No se encontraron herramientas con nombre: "{searchToolName}"
                                    </Alert>
                                )}

                                {!searchToolName && filteredTools.length === 0 && (
                                    <Alert severity="info" sx={{ mt: 2 }}>
                                        Ingresa un nombre para buscar herramientas
                                    </Alert>
                                )}

                                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 'auto', pt: 3 }}>
                                    <Button
                                        variant="outlined"
                                        onClick={goToPreviousStep}
                                        sx={{ color: '#0A142E', borderColor: '#0A142E' }}
                                    >
                                        Atrás
                                    </Button>
                                </Box>
                            </Box>
                        )}

                        {/* PASO 3: Ingresar Fechas */}
                        {activeStep === 2 && (
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
                                <Alert severity="info">
                                    Cliente: <strong>{selectedCustomer.name}</strong> ({formatRut(selectedCustomer.rut)})
                                    <br />
                                    Herramienta: <strong>{selectedTool.name}</strong> (ID: {selectedTool.id})
                                </Alert>

                                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 600, mb: 1 }}>
                                    Paso 3: Ingresar Fechas del Préstamo
                                </Typography>

                                <Box>
                                    <Typography variant="caption" sx={{ color: '#666', display: 'block', mb: 1, fontWeight: 500 }}>
                                        Fecha de Préstamo
                                    </Typography>
                                    <TextField
                                        fullWidth
                                        type="date"
                                        value={getTodayDate()}
                                        disabled
                                        InputLabelProps={{ shrink: true }}
                                        variant="outlined"
                                        size="medium"
                                    />
                                    <Typography variant="caption" sx={{ color: '#666', display: 'block', mt: 0.5 }}>
                                        Se establece automáticamente como hoy
                                    </Typography>
                                </Box>

                                <Box>
                                    <Typography variant="caption" sx={{ color: '#d32f2f', display: 'block', mb: 1, fontWeight: 500 }}>
                                        Fecha de Devolución *
                                    </Typography>
                                    <Box
                                        onClick={() => document.getElementById('return-date-picker').showPicker?.()}
                                        sx={{ cursor: 'pointer' }}
                                    >
                                        <TextField
                                            id="return-date-picker"
                                            fullWidth
                                            type="date"
                                            label="Selecciona fecha de devolución"
                                            value={returnDate}
                                            onChange={(e) => setReturnDate(e.target.value)}
                                            InputLabelProps={{ shrink: true }}
                                            variant="outlined"
                                            size="medium"
                                            inputProps={{ min: getTodayDate() }}
                                            sx={{
                                                '& input': {
                                                    cursor: 'pointer'
                                                }
                                            }}
                                        />
                                    </Box>
                                </Box>

                                <Alert severity="info">
                                    El estado del préstamo se establecerá automáticamente como <strong>"activo"</strong>
                                </Alert>

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
                                        disabled={loading || !returnDate}
                                        sx={{
                                            backgroundColor: '#0A142E',
                                            color: '#FACC15',
                                            fontWeight: 600,
                                            '&:hover': { backgroundColor: '#1a2847' },
                                            '&:disabled': { backgroundColor: '#e0e0e0', color: '#9e9e9e' }
                                        }}
                                    >
                                        {loading ? <CircularProgress size={20} sx={{ color: '#FACC15', mr: 1 }} /> : null}
                                        {loading ? 'Creando...' : 'Crear Préstamo'}
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

export default AddLoan;