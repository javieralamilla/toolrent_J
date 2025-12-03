import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Button,
    Alert,
    CircularProgress,
    Grid,
    Chip
} from '@mui/material';
import {
    Clear as ClearIcon,
    EmojiEvents as TrophyIcon
} from '@mui/icons-material';
import loanService from '../services/loan.service';

const ToolsRanking = () => {
    const [ranking, setRanking] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Filtros de fecha
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    // Función para formatear fechas
    const formatDate = (dateString) => {
        const [year, month, day] = dateString.split('-');
        return `${day}-${month}-${year}`;
    };

    // Función para cargar el ranking de herramientas
    const fetchToolsRanking = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await loanService.getMostRentedTools(
                startDate || null,
                endDate || null
            );

            // La respuesta viene como Array de [toolName, categoryName, loanCount]
            const formattedData = response.data.map((item, index) => ({
                position: index + 1,
                toolName: item[0],
                categoryName: item[1],
                loanCount: item[2]
            }));

            setRanking(formattedData);
        } catch (err) {
            setError('Error al cargar el ranking de herramientas');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Cargar ranking al montar el componente
    useEffect(() => {
        fetchToolsRanking();
    }, []);

    // Limpiar filtros
    const handleClearFilters = () => {
        setStartDate('');
        setEndDate('');
        fetchToolsRanking(); // Recargar sin filtros de fecha
    };

    // Buscar con filtros de fecha
    const handleSearchWithDateFilter = () => {
        fetchToolsRanking();
    };

    // Obtener color de medalla según posición
    const getMedalColor = (position) => {
        switch(position) {
            case 1:
                return '#FFD700'; // Oro
            case 2:
                return '#C0C0C0'; // Plata
            case 3:
                return '#CD7F32'; // Bronce
            default:
                return '#757575'; // Gris
        }
    };

    // Obtener chip de posición
    const getPositionChip = (position) => {
        if (position <= 3) {
            return (
                <Chip
                    icon={<TrophyIcon />}
                    label={`#${position}`}
                    sx={{
                        backgroundColor: getMedalColor(position),
                        color: 'white',
                        fontWeight: 'bold'
                    }}
                    size="small"
                />
            );
        }
        return (
            <Chip
                label={`#${position}`}
                color="default"
                size="small"
            />
        );
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '400px' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 4 }}>
                Ranking de Herramientas Más Prestadas
            </Typography>

            {/* Sección de Filtros */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 'bold', mb: 2 }}>
                    Filtros por Fecha
                </Typography>

                <Grid container spacing={2} alignItems="center">
                    {/* Filtro fecha inicio */}
                    <Grid size={{ xs: 12, md: 4 }}>
                        <Box
                            onClick={() => document.getElementById('start-date-picker-ranking').showPicker?.()}
                            sx={{ cursor: 'pointer' }}
                        >
                            <TextField
                                id="start-date-picker-ranking"
                                fullWidth
                                label="Fecha Inicio"
                                type="date"
                                value={startDate}
                                onChange={(e) => setStartDate(e.target.value)}
                                InputLabelProps={{ shrink: true }}
                                sx={{
                                    '& input': {
                                        cursor: 'pointer'
                                    }
                                }}
                            />
                        </Box>
                    </Grid>
                    {/* Filtro fecha fin */}
                    <Grid size={{ xs: 12, md: 4 }}>
                        <Box
                            onClick={() => document.getElementById('end-date-picker-ranking').showPicker?.()}
                            sx={{ cursor: 'pointer' }}
                        >
                            <TextField
                                id="end-date-picker-ranking"
                                fullWidth
                                label="Fecha Fin"
                                type="date"
                                value={endDate}
                                onChange={(e) => setEndDate(e.target.value)}
                                InputLabelProps={{ shrink: true }}
                                sx={{
                                    '& input': {
                                        cursor: 'pointer'
                                    }
                                }}
                            />
                        </Box>
                    </Grid>

                    <Grid size={{ xs: 12, md: 2 }}>
                        <Button
                            fullWidth
                            variant="contained"
                            onClick={handleSearchWithDateFilter}
                            sx={{
                                height: '56px',
                                backgroundColor: '#0A142E',
                                '&:hover': {
                                    backgroundColor: '#1a2847'
                                }
                            }}
                        >
                            Buscar
                        </Button>
                    </Grid>

                    <Grid size={{ xs: 12, md: 2 }}>
                        <Button
                            fullWidth
                            variant="outlined"
                            startIcon={<ClearIcon />}
                            onClick={handleClearFilters}
                            sx={{ height: '56px' }}
                        >
                            Limpiar
                        </Button>
                    </Grid>
                </Grid>

                {/* Estadísticas rápidas */}
                <Box sx={{ mt: 3, display: 'flex', gap: 3, flexWrap: 'wrap' }}>
                    <Box>
                        <Typography variant="body2" color="textSecondary">
                            Total de Herramientas
                        </Typography>
                        <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                            {ranking.length}
                        </Typography>
                    </Box>
                    <Box>
                        <Typography variant="body2" color="textSecondary">
                            Total de Préstamos
                        </Typography>
                        <Typography variant="h6" sx={{ color: '#4caf50', fontWeight: 'bold' }}>
                            {ranking.reduce((sum, item) => sum + item.loanCount, 0)}
                        </Typography>
                    </Box>
                    {ranking.length > 0 && (
                        <Box>
                            <Typography variant="body2" color="textSecondary">
                                Herramienta Más Prestada
                            </Typography>
                            <Typography variant="h6" sx={{ color: '#FFD700', fontWeight: 'bold' }}>
                                {ranking[0].toolName}
                            </Typography>
                        </Box>
                    )}
                    {ranking.length > 0 && (
                        <Box>
                            <Typography variant="body2" color="textSecondary">
                                Préstamos Top 1
                            </Typography>
                            <Typography variant="h6" sx={{ color: '#FFD700', fontWeight: 'bold' }}>
                                {ranking[0].loanCount}
                            </Typography>
                        </Box>
                    )}
                </Box>
            </Paper>

            {/* Mensaje de error */}
            {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            {/* Tabla de ranking */}
            <TableContainer component={Paper}>
                <Table>
                    <TableHead sx={{ backgroundColor: '#0A142E' }}>
                        <TableRow>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Posición</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Herramienta</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Categoría</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold', textAlign: 'center' }}>
                                Cantidad de Préstamos
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {ranking.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={4} align="center">
                                    <Typography variant="body1" sx={{ py: 3, color: '#666' }}>
                                        No se encontraron datos para el ranking con los filtros aplicados
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            ranking.map((item) => (
                                <TableRow
                                    key={item.position}
                                    sx={{
                                        '&:hover': { backgroundColor: '#f5f5f5' },
                                        backgroundColor: item.position <= 3 ? 'rgba(255, 215, 0, 0.05)' : 'inherit'
                                    }}
                                >
                                    <TableCell>
                                        {getPositionChip(item.position)}
                                    </TableCell>
                                    <TableCell>
                                        <Typography
                                            sx={{
                                                fontWeight: item.position <= 3 ? 'bold' : 'normal',
                                                color: item.position === 1 ? '#FFD700' : 'inherit'
                                            }}
                                        >
                                            {item.toolName}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>{item.categoryName}</TableCell>
                                    <TableCell align="center">
                                        <Chip
                                            label={item.loanCount}
                                            color="primary"
                                            size="small"
                                        />
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default ToolsRanking;